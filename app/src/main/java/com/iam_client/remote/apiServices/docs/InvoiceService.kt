package com.iam_client.remote.apiServices.docs

import com.iam_client.remote.data.docs.InvoiceDTO
import com.iam_client.remote.interceptors.READ_TIMEOUT
import retrofit2.http.*

interface InvoiceService{

    @GET("Invoices/all/{cid}")//return null items
    suspend fun getAll(@Path("cid")cid : String,
                       @Query("updatedAfter") updatedAfter: Long?) : List<InvoiceDTO>

    @GET("Invoices/BySalesman/{salesmanCode}")//return null items
    suspend fun getAll(@Path("salesmanCode")salesmanCode : Int,
                       @Query("openOnly") openOnly: Boolean,
                       @Query("updatedAfter") updatedAfter: Long?) : List<InvoiceDTO>

    @GET("Invoices/{sn}")//load the items
    suspend fun get(@Path("sn")sn : Int) : InvoiceDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @POST("Invoices")
    suspend fun addNew(@Body document : InvoiceDTO) : InvoiceDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @PUT("Invoices")
    suspend fun update(@Body document : InvoiceDTO) : InvoiceDTO
}


class BaseInvoiceService(private val service: InvoiceService): IDocumentService<InvoiceDTO>{
    override suspend fun getAll(salesmenCode: Int, openOnly: Boolean, updatedAfter: Long?): List<InvoiceDTO> =
        service.getAll(salesmenCode,openOnly,updatedAfter)
    override suspend fun addNew(document: InvoiceDTO) = service.addNew(document)
    override suspend fun update(document: InvoiceDTO) = service.update(document)

    override suspend fun getAll(cid: String, updatedAfter: Long?): List<InvoiceDTO> = service.getAll(cid,updatedAfter)
    override suspend fun get(sn: Int): InvoiceDTO = service.get(sn)
}


