package com.iam_client.remote.apiServices

import com.iam_client.remote.data.CardGroupDTO
import com.iam_client.remote.data.CustomerBalanceRecordDTO
import com.iam_client.remote.data.CustomerDTO
import com.iam_client.remote.interceptors.READ_TIMEOUT
import retrofit2.http.*

interface CustomerApiService {

    @Headers(value =["$READ_TIMEOUT:20000"])
    @GET("Customer/All")
    suspend fun getAllCustomers(@Query("updatedAfter")updatedAfter: Long? = null) : List<CustomerDTO>

    @GET("Customer/count")
    suspend fun countCustomers() : Long

    @GET("Customer")
    suspend fun  getCustomersWithPaging(@Query("page") page: Long,
                               @Query("size") size: Long) : Array<CustomerDTO>


    @GET("Customer/GetByCID/{cid}")
    suspend fun getCustomerByCid(@Path("cid")cid: String ) : CustomerDTO


    @GET("Customer/groups")
    suspend fun getAllGroups() : Array<CardGroupDTO>

    @Headers(value =["$READ_TIMEOUT:35000"])
    @PUT("Customer/{cid}")
    suspend fun updateCustomer(@Path("cid") cid:String,@Body customer :CustomerDTO) : CustomerDTO


    @Headers(value =["$READ_TIMEOUT:35000"])
    @POST("Customer")
    suspend fun addCustomer(@Body customer :CustomerDTO) : CustomerDTO

    @GET("Customer/Balance/{cid}")
    suspend fun getCustomerBalanceDetails(@Path("cid") cid:String):Array<CustomerBalanceRecordDTO>


}