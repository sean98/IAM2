package com.iam_client.remote.apiServices.docs

import com.iam_client.remote.data.docs.DeliveryNoteDTO
import com.iam_client.remote.interceptors.READ_TIMEOUT
import retrofit2.http.*

interface DeliveryNoteService {
    @GET("DeliveryNotes/all/{cid}")//return null items
    suspend fun getAll(@Path("cid")cid : String,
                       @Query("updatedAfter") updatedAfter: Long?) : List<DeliveryNoteDTO>

    @GET("DeliveryNotes/BySalesman/{salesmanCode}")//return null items
    suspend fun getAll(@Path("salesmanCode")salesmanCode : Int,
                       @Query("openOnly") openOnly: Boolean,
                       @Query("updatedAfter") updatedAfter: Long?) : List<DeliveryNoteDTO>

    @GET("DeliveryNotes/{sn}")//load the items
    suspend fun get(@Path("sn")sn : Int) : DeliveryNoteDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @POST("DeliveryNotes")
    suspend fun addNew(@Body document : DeliveryNoteDTO) : DeliveryNoteDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @PUT("DeliveryNotes")
    suspend fun update(@Body document : DeliveryNoteDTO) : DeliveryNoteDTO
}

class BaseDeliveryNoteService(private val service: DeliveryNoteService): IDocumentService<DeliveryNoteDTO>{
    override suspend fun getAll(salesmenCode: Int, openOnly: Boolean, updatedAfter: Long?): List<DeliveryNoteDTO> =
        service.getAll(salesmenCode,openOnly,updatedAfter)
    override suspend fun update(document: DeliveryNoteDTO) = service.update(document)

    override suspend fun addNew(document: DeliveryNoteDTO) = service.addNew(document)
    override suspend fun getAll(cid: String, updatedAfter: Long?): List<DeliveryNoteDTO> = service.getAll(cid,updatedAfter)
    override suspend fun get(sn: Int): DeliveryNoteDTO = service.get(sn)
}


