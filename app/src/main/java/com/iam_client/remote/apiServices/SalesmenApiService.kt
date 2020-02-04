package com.iam_client.remote.apiServices

import com.iam_client.remote.data.SalesmanDTO
import retrofit2.http.GET

interface SalesmenApiService {
    @GET("salesman/All")
    suspend fun getAll() : Array<SalesmanDTO>


    //TODO salesman history + salesman open documents etc...
}