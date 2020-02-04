package com.iam_client.remote.apiServices.products

import com.iam_client.remote.data.products.ProductDTO
import com.iam_client.remote.data.products.ProductCategoryDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApiService {

    @GET("Products")
    suspend fun getAllProducts(@Query("updatedAfter")updatedAfter: Long? = null) :Array<ProductDTO>
    @GET("Products/{code}")
    suspend fun getProduct(@Path("code")code:String) :ProductDTO


    @GET("Products/Groups")
    suspend fun getAllProductCategories(@Query("updatedAfter")updatedAfter: Long? = null) :Array<ProductCategoryDTO>
    @GET("Products/Groups/{code}")
    suspend fun getProductCategory(@Path("code")code:Int) :ProductCategoryDTO
}