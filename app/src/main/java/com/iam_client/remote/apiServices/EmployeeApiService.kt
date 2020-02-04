package com.iam_client.remote.apiServices

import com.iam_client.remote.data.EmployeeDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EmployeeApiService {
    @GET("Employee")
    suspend fun getEmployeesPaging(@Query("page")page: Long,@Query("size")size : Int) : List<EmployeeDTO>

    @GET("Employee/GetBySN/{sn}")
    suspend fun getEmployee(@Path("sn")sn: Int) : EmployeeDTO


}