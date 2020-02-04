package com.iam_client.repostories.customer

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.iam_client.repostories.data.CardGroup
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.CustomerBalanceRecord
import com.iam_client.repostories.utils.RepositoryCall

interface CustomerRepository {

    fun getFilteredCustomersWithPagination(filter: String): LiveData<PagedList<Customer>>

    suspend fun refreshCustomers()

    fun getCachedCustomerById(cid: String):LiveData<Customer>
    suspend fun refreshCustomerByCid(cid : String) : LiveData<Customer>

    suspend fun getCardGroup(code:Int): CardGroup
    suspend fun update(customer: Customer) :Customer
    suspend fun add(customer: Customer) : Customer
    val allCardGroupsLive :LiveData<List<CardGroup>>
    suspend fun getCardGroups():List<CardGroup>

    fun getCachedCardGroup(groupCode : Int):LiveData<CardGroup>
    suspend fun refreshBalanceRecordsCache(customer: Customer)
    fun getCachedBalanceRecordsWithPagination(customer: Customer, nonZeroOnly:Boolean) : LiveData<PagedList<CustomerBalanceRecord>>

}