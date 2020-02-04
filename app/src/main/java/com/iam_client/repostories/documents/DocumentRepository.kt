package com.iam_client.repostories.documents

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.Salesman
import com.iam_client.repostories.data.docs.Document

interface DocumentRepository<TDoc :  Document>{
    fun getAllByCustomerWithPagination(customer: Customer): LiveData<PagedList<TDoc>>

    fun getAllBySalesmanWithPagination(salesman: Salesman,openOnly : Boolean): LiveData<PagedList<TDoc>>

    suspend fun refreshAllBySalesmen(salesman: Salesman, openOnly: Boolean): List<TDoc>

    suspend fun refreshAllByCustomer(customer: Customer): List<TDoc>

    fun getCachedBySn(sn: Int): LiveData<TDoc>

    suspend fun refreshBySn(sn: Int)

    suspend fun addNew(document: TDoc):TDoc

    suspend fun update(document: TDoc): TDoc
}


