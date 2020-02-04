package com.iam_client.remote.apiServices.docs

import com.iam_client.remote.data.docs.QuotationDTO
import com.iam_client.remote.interceptors.CONNECT_TIMEOUT
import com.iam_client.remote.interceptors.READ_TIMEOUT
import com.iam_client.remote.interceptors.WRITE_TIMEOUT
import retrofit2.http.*

interface QuotationService{
    @GET("Quotations/all/{cid}")//return null items
    suspend fun getAll(@Path("cid")cid : String,
                       @Query("updatedAfter") updatedAfter: Long?) : List<QuotationDTO>


    @GET("Quotations/BySalesman/{salesmanCode}")//return null items
    suspend fun getAll(@Path("salesmanCode")salesmanCode : Int,
                       @Query("openOnly") openOnly: Boolean,
                       @Query("updatedAfter") updatedAfter: Long?) : List<QuotationDTO>
    @GET("Quotations/{sn}")//load the items
    suspend fun get(@Path("sn")sn : Int) : QuotationDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @POST("Quotations")
    suspend fun addNew(@Body document : QuotationDTO) : QuotationDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @PUT("Quotations")
    suspend fun update(@Body document : QuotationDTO) : QuotationDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @GET("Quotations/Cancel/{sn}")
    suspend fun cancel(@Path("sn")sn:Int) : QuotationDTO
}


class BaseQuotationService(private val service: QuotationService): IDocumentService<QuotationDTO>{
    override suspend fun getAll(salesmenCode: Int, openOnly: Boolean, updatedAfter: Long?): List<QuotationDTO> =
        service.getAll(salesmenCode,openOnly,updatedAfter)

    override suspend fun addNew(document: QuotationDTO) = service.addNew(document)
    override suspend fun update(document: QuotationDTO) = service.update(document)
    override suspend fun getAll(cid: String, updatedAfter: Long?): List<QuotationDTO> = service.getAll(cid,updatedAfter)
    override suspend fun get(sn: Int): QuotationDTO = service.get(sn)
}


