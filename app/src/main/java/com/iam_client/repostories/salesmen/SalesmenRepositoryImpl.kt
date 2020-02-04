package com.iam_client.repostories.salesmen

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.iam_client.local.dao.SalesmanDao
import com.iam_client.repostories.utils.mappers.*
import com.iam_client.remote.apiServices.SalesmenApiService
import com.iam_client.repostories.data.Salesman
import kotlinx.coroutines.*

class SalesmenRepositoryImpl(
    private val salesmenApiService: SalesmenApiService,
    private val salesmanDao: SalesmanDao
) : SalesmenRepository {




    override val allSalesmanLive: LiveData<List<Salesman>> =
        Transformations.map(salesmanDao.getAllLive()) { list -> list.map { it.mapToModel() } }
    override val cachedSalesmen : MutableMap<Int, Salesman> = runBlocking {//TODO add error handler
       val s  = mutableMapOf<Int,Salesman>()
        s.putAll(salesmanDao.getAll().map { it.sn to it.mapToModel() })
        s
    }

    override suspend fun refreshSalesmen() {
        val salesmen = salesmenApiService.getAll()
        //TODO catch and map exceptions
        val salesmenEntities = salesmen.map { it.mapToEntity() }
        salesmanDao.upsertAll(salesmenEntities)
        cachedSalesmen.putAll(salesmenEntities.map { it.sn to it.mapToModel() })
    }




    override suspend fun getAll(): List<Salesman> = withContext(Dispatchers.IO) {
        //try awaitAndGet from cache
        val salesmen = cachedSalesmen.map { it.value }
        if (!salesmen.isNullOrEmpty())
            refreshSalesmen()
        //is forced online or cache invalid
        return@withContext cachedSalesmen.map { it.value }
    }

    override fun getCachedSalesmanBySNLive(sn: Int): LiveData<Salesman> {
        return Transformations.map(salesmanDao.findBySN(sn)) { it?.mapToModel() }
    }

    override suspend fun getSalesman(sn: Int): Salesman = withContext(Dispatchers.IO) {
        val cachedSalesman = cachedSalesmen.map { it.value }.firstOrNull { it.sn == sn }
        if (cachedSalesman != null)
            return@withContext cachedSalesman as Salesman /*BUG*/
        refreshSalesmen()
        return@withContext cachedSalesmen.map { it.value }.first { it.sn == sn }
    }

    //newInstance singleton instance
    companion object {
        @Volatile
        private var instance: SalesmenRepositoryImpl? = null

        fun getInstance(salesmenApiService: SalesmenApiService, salesmanDao: SalesmanDao) =
            instance ?: synchronized(this) {
                instance ?: SalesmenRepositoryImpl(salesmenApiService, salesmanDao)
                    .also { instance = it }
            }
    }


}