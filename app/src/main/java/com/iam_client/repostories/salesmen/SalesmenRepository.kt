package com.iam_client.repostories.salesmen

import androidx.lifecycle.LiveData
import com.iam_client.repostories.data.Salesman
import com.iam_client.repostories.utils.RepositoryCall

interface SalesmenRepository {
    val allSalesmanLive: LiveData<List<Salesman>>
    val cachedSalesmen:Map<Int,Salesman>
    suspend fun getAll():List<Salesman>
    suspend fun getSalesman(sn:Int) : Salesman
    fun getCachedSalesmanBySNLive(sn : Int) : LiveData<Salesman>
    suspend fun refreshSalesmen()
}