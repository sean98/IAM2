package com.iam_client.remote.apiServices

import com.iam_client.remote.interceptors.NO_AUTHORIZATION_HEADER
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface LoginApiService {
    @GET("DemoAuth/login")
    @Headers("$NO_AUTHORIZATION_HEADER:true")
    suspend fun login(@Header("username")username: String,
              @Header("password") password: String) : String
}