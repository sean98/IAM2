package com.iam_client.remote.apiServices.docs

import com.iam_client.remote.data.docs.DocumentDTO

interface IDocumentService<TDoc> where  TDoc : DocumentDTO {

    suspend fun getAll(cid : String, updatedAfter: Long? = null) : List<TDoc>

    suspend fun getAll(salesmenCode: Int,openOnly:Boolean, updatedAfter: Long? = null) : List<TDoc>

    suspend fun get(sn : Int) : TDoc

    suspend fun addNew(document: TDoc):TDoc

    suspend fun update(document: TDoc):TDoc
}





