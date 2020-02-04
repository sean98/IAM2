package com.iam_client.remote.apiServices.docs

import com.iam_client.remote.data.docs.CreditNoteDTO
import com.iam_client.remote.interceptors.READ_TIMEOUT
import retrofit2.http.*

interface CreditNoteService  {
    @GET("CreditNotes/all/{cid}")//return null items
    suspend fun getAll(@Path("cid")cid : String,
                       @Query("updatedAfter") updatedAfter: Long?) : List<CreditNoteDTO>

    @GET("CreditNotes/BySalesman/{salesmanCode}")//return null items
    suspend fun getAll(@Path("salesmanCode")salesmanCode : Int,
                       @Query("openOnly") openOnly: Boolean,
                       @Query("updatedAfter") updatedAfter: Long?) : List<CreditNoteDTO>

    @GET("CreditNotes/{sn}")//load the items
    suspend fun get(@Path("sn")sn : Int) : CreditNoteDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @POST("CreditNotes")
    suspend fun addNew(@Body document : CreditNoteDTO) : CreditNoteDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @PUT("CreditNotes")
    suspend fun update(@Body document : CreditNoteDTO) : CreditNoteDTO
}


class BaseCreditNoteService(private val service: CreditNoteService): IDocumentService<CreditNoteDTO>{
    override suspend fun getAll(salesmenCode: Int, openOnly: Boolean, updatedAfter: Long?): List<CreditNoteDTO> =
        service.getAll(salesmenCode,openOnly,updatedAfter)
    override suspend fun update(document: CreditNoteDTO) = service.update(document)

    override suspend fun addNew(document: CreditNoteDTO) = service.addNew(document)
    override suspend fun getAll(cid: String, updatedAfter: Long?): List<CreditNoteDTO> = service.getAll(cid,updatedAfter)
    override suspend fun get(sn: Int): CreditNoteDTO = service.get(sn)
}


