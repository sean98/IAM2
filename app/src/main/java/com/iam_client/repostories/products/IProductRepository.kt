package com.iam_client.repostories.products

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.iam_client.repostories.data.products.Product
import com.iam_client.repostories.data.products.ProductCategory
import com.iam_client.repostories.data.products.ProductProperty

interface IProductRepository {

    fun getProductCategoriesWithPagination(filter: String? = null,onlyWithActiveProducts : Boolean?=null): LiveData<PagedList<ProductCategory>>

    fun getProductsWithPagination(filter: String? = null, category: ProductCategory?=null,activeOnly:Boolean?=null): LiveData<PagedList<Product>>

    suspend fun refreshProductsAndCategories()

    suspend fun getProduct(code:String):Product

     fun getPropertiesLocalName(properties : List<ProductProperty>) :LiveData<Map<ProductProperty,String?>>
}