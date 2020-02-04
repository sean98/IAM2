package com.iam_client.remote.apiServices.docs

import com.iam_client.remote.data.docs.OrderDTO
import com.iam_client.remote.interceptors.READ_TIMEOUT
import retrofit2.http.*

interface OrderService  {
    @GET("Orders/all/{cid}")//return null items
    suspend fun getAll(@Path("cid")cid : String,
                              @Query("updatedAfter") updatedAfter: Long?) : List<OrderDTO>

    @GET("Orders/BySalesman/{salesmanCode}")//return null items
    suspend fun getAll(@Path("salesmanCode")salesmanCode : Int,
                       @Query("openOnly") openOnly: Boolean,
                       @Query("updatedAfter") updatedAfter: Long?) : List<OrderDTO>


    @GET("Orders/{sn}")//load the items
    suspend fun get(@Path("sn")sn : Int) : OrderDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @POST("Orders")
    suspend fun addNew(@Body document : OrderDTO) : OrderDTO

    @Headers(value =["$READ_TIMEOUT:35000"])
    @PUT("Orders")
    suspend fun update(@Body document : OrderDTO) : OrderDTO
}

class BaseOrderService(private val service: OrderService): IDocumentService<OrderDTO>{
    override suspend fun getAll(salesmenCode: Int, openOnly: Boolean, updatedAfter: Long?): List<OrderDTO> =
        service.getAll(salesmenCode,openOnly,updatedAfter)
    override suspend fun addNew(document: OrderDTO) = service.addNew(document)
    override suspend fun update(document: OrderDTO) = service.update(document)

    override suspend fun getAll(cid: String, updatedAfter: Long?): List<OrderDTO> = service.getAll(cid,updatedAfter)
    override suspend fun get(sn: Int): OrderDTO = service.get(sn)
}


