package com.iam_client.repostories.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.iam_client.local.dao.CustomerDao
import com.iam_client.remote.apiServices.CustomerApiService
import com.iam_client.repostories.data.CardGroup
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.CustomerBalanceRecord
import com.iam_client.repostories.salesmen.SalesmenRepository
import com.iam_client.repostories.utils.mappers.*
import kotlinx.coroutines.*

class CustomerRepositoryImpl private constructor(
    private val customerDao: CustomerDao,
    private val customerApiService: CustomerApiService,
    private val salesmenRepository: SalesmenRepository
) : CustomerRepository {


    private val cachedSalesmen = salesmenRepository.cachedSalesmen
    private val cacheCardGroups = runBlocking{//TODO add error handler
        val map: MutableMap<Int, CardGroup> = mutableMapOf()
        map.putAll(customerDao.getAllCardGroups().map { it.sn to it.mapToModel() })
        map
    }


    override val allCardGroupsLive: LiveData<List<CardGroup>> =
        Transformations.map(customerDao.getAllCardGroupsLive()) { it.map { e -> e.mapToModel() } }

    override fun getFilteredCustomersWithPagination(filter: String): LiveData<PagedList<Customer>> {
        val factory =
            if (filter.isNotBlank())
                customerDao.getCustomersPaged(filter).map { it.mapToModel(cacheCardGroups, cachedSalesmen) }
            else customerDao.getCustomersPaged().map { it.mapToModel(cacheCardGroups, cachedSalesmen) }

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(10) //.setPrefetchDistance(1)
            .build()

        return LivePagedListBuilder(factory, config)
            .build()
    }

    private var lastCustomersRefreshDateTime: Long? = null
    override suspend fun refreshCustomers() = withContext(Dispatchers.IO) {
        var lastUpdateDateTime = customerDao.getLastUpdated()
        if (lastCustomersRefreshDateTime != null && lastUpdateDateTime != null)
            lastUpdateDateTime = lastCustomersRefreshDateTime

        val refreshedCustomer = customerApiService.getAllCustomers(lastUpdateDateTime)

        //check if card groups and salesmen are consist before adding to local db
        if(refreshedCustomer.any{!cacheCardGroups.containsKey( it.groupSN)})
            refreshCardGroupsCache()
        if(refreshedCustomer.any{!cachedSalesmen.containsKey( it.salesmanCode)})
            salesmenRepository.refreshSalesmen()

        //TODO catch and map exceptions
        customerDao.upsertAll(refreshedCustomer.map { it.mapToEntity() })

        lastCustomersRefreshDateTime = System.currentTimeMillis() / 1000L//to unix timestamp
    }


    private suspend fun refreshCardGroupsCache() = withContext(Dispatchers.IO) {
        val groups = customerApiService.getAllGroups()
        //TODO catch and map exceptions
        val groupsEntities = groups.map { it.mapToEntity() }
        customerDao.rebuildCardGroupData(groupsEntities)//delete all groups and reallocate cache
        cacheCardGroups.putAll(groupsEntities.map { it.sn to it.mapToModel() })
    }


    override fun getCachedCustomerById(cid: String): LiveData<Customer> {
        return Transformations.map(customerDao.getCustomerByCid(cid)) {
            it?.mapToModel(
                cacheCardGroups,
                cachedSalesmen
            )
        }
    }

    override suspend fun refreshCustomerByCid(cid: String): LiveData<Customer> = withContext(Dispatchers.IO) {
        //TODO handle exceptions - map to internal
        val customer = customerApiService.getCustomerByCid(cid)
            .mapToEntity()
        //upsert customer to cache
        customerDao.upsert(customer)

        Transformations.map(customerDao.getCustomerByCid(cid)) { it!!.mapToModel(cacheCardGroups, cachedSalesmen) }
    }

    override suspend fun update(customer: Customer): Customer = withContext(Dispatchers.IO) {
        if (customer.cid == null)
            throw RuntimeException("cant update customer with no cid")
        val customerDto = customer.mapToDTO()
        //TODO handle exceptions - map to internal
        val response = customerApiService.updateCustomer(customerDto.cid!!, customerDto)
        val cEntity = response.mapToEntity()
        customerDao.upsert(cEntity)
        cEntity.mapToModel(cacheCardGroups, cachedSalesmen)
    }

    override suspend fun add(customer: Customer) = withContext(Dispatchers.IO) {
        val customerDto = customer.mapToDTO()
        if (customerDto.cid != null)
            throw RuntimeException("cant add customer with cid")
        //TODO handle exceptions - map to internal
        val response = customerApiService.addCustomer(customerDto)
        val cEntity = response.mapToEntity()
        customerDao.upsert(cEntity)
        cEntity.mapToModel(cacheCardGroups, cachedSalesmen)
    }

    override fun getCachedCardGroup(groupCode: Int): LiveData<CardGroup> {
        return Transformations.map(customerDao.getCardGroupLive(groupCode)) { it.mapToModel() }
    }

    override suspend fun getCardGroups(): List<CardGroup> = withContext(Dispatchers.IO) {
        if (cacheCardGroups.map { it.value }.isEmpty())
            refreshCardGroupsCache()
        return@withContext cacheCardGroups.map { it.value }
    }

    override suspend fun getCardGroup(code: Int): CardGroup = withContext(Dispatchers.IO) {
        val cachedGroup = cacheCardGroups.map { it.value }.firstOrNull { it.sn == code }
        if (cachedGroup != null)
            return@withContext cachedGroup as CardGroup/*BUG*/
        refreshCardGroupsCache()
        return@withContext cacheCardGroups.map { it.value }.first { it.sn == code }
    }


    override suspend fun refreshBalanceRecordsCache(customer: Customer) = withContext(Dispatchers.IO) {
        //TODO handle exceptions - map to internal
        //todo improve performance by only ask for new record from server (not always awaitAndGet all)
        val records = customerApiService.getCustomerBalanceDetails(customer.cid!!)
            .map { it.mapToEntity() }
        //upsert recordsLiveSource to cache
        customerDao.upsertAllBalanceRecords(records)
        //refresh customer cache
        customerDao.upsert(
            customerApiService.getCustomerByCid(customer.cid)
                .mapToEntity()
        )

    }

    override fun getCachedBalanceRecordsWithPagination(
        customer: Customer, nonZeroOnly: Boolean
    ): LiveData<PagedList<CustomerBalanceRecord>> {
        val factory = customerDao.getAllBalanceRecordsPage(customer.cid!!, nonZeroOnly).map { c ->
            c.mapToModel()
        }
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(10) //.setPrefetchDistance(1)
            .build()
        return LivePagedListBuilder(factory, config).build()
    }


    //Singleton
    companion object {
        @Volatile
        private var instance: CustomerRepositoryImpl? = null

        fun getInstance(
            customerDao: CustomerDao,
            customerApiService: CustomerApiService,
            salesmenRepository: SalesmenRepository
        ) =
            instance ?: synchronized(this) {
                instance ?: CustomerRepositoryImpl(customerDao, customerApiService, salesmenRepository)
                    .also { instance = it }
            }
    }
}
