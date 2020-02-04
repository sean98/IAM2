package com.iam_client.local.dao.products

import androidx.paging.DataSource
import androidx.room.*
import com.iam_client.local.data.products.ProductEntity
import com.iam_client.local.data.products.ProductCategoryEntity

@Dao
abstract class ProductsDao {

    ///Products///
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertAll(products: List<ProductEntity>)

    @Query("DELETE FROM PRODUCTS")
    abstract suspend fun deleteAll()

    @Query("SELECT MAX(CASE WHEN lastUpdateDateTime = null then creationDateTime Else lastUpdateDateTime END) FROM PRODUCTS")
    abstract suspend fun getLastUpdatedProductTime(): Long?


    @Query("""SELECT * FROM PRODUCTS  WHERE 
        ((:filter not null AND name like '%' ||:filter||'%') OR(:filter is null))AND 
         ((:activeOnly not null AND isActive = :activeOnly) OR(:activeOnly is null))AND 
        ((:groupCode not null AND groupCode = :groupCode) OR(:groupCode is null ))""")
    abstract fun getAllProductsWithPaging(filter : String? = null,groupCode: Int? = null ,activeOnly:Boolean?=null)
            : DataSource.Factory<Int, ProductEntity>

    @Query("""SELECT * FROM PRODUCTS  WHERE 
        ((:filter not null AND name like '%' ||:filter||'%') OR(:filter is null)) AND 
         ((:activeOnly not null AND isActive = :activeOnly) OR(:activeOnly is null))AND 
        ((:groupCode not null AND groupCode = :groupCode) OR(:groupCode is null ))""")
    abstract suspend fun getAllProducts(filter : String? = null,groupCode: Int? = null ,activeOnly:Boolean?=null) : List<ProductEntity>

    @Query("SELECT * FROM PRODUCTS WHERE code = :productCode")
    abstract suspend fun getProduct(productCode:String):ProductEntity?


    ///Product Groups///
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertCategory(productCategory: ProductCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertCategories(productCategories: List<ProductCategoryEntity>)

    @Query("DELETE FROM PRODUCT_CATEGORIES")
    abstract suspend fun deleteAllCategories()

    @Query("SELECT MAX(CASE WHEN lastUpdateDate= null then creationDate Else lastUpdateDate END) FROM PRODUCT_CATEGORIES")
    abstract suspend fun getLastUpdatedCategoryTime(): Long?

    @Query("""SELECT * FROM PRODUCT_CATEGORIES WHERE 
        ((:filter not null AND name like '%' ||:filter||'%') OR(:filter is null))AND 
        ((:onlyWithActiveProducts not null AND EXISTS 
            (SELECT * FROM PRODUCTS WHERE groupCode = PRODUCT_CATEGORIES.code AND PRODUCTS.isActive = 1)
            ) OR(:onlyWithActiveProducts is null)) """)
    abstract fun getAllCategoriesWithPaging(filter : String? = null,onlyWithActiveProducts : Boolean?=null)
            : DataSource.Factory<Int, ProductCategoryEntity>

    @Query("""SELECT * FROM PRODUCT_CATEGORIES WHERE 
        ((:filter not null AND name like '%' ||:filter||'%') OR(:filter is null))AND 
        ((:onlyWithActiveProducts not null AND EXISTS 
            (SELECT * FROM PRODUCTS WHERE groupCode = PRODUCT_CATEGORIES.code AND PRODUCTS.isActive = 1)
            ) OR(:onlyWithActiveProducts is null))""")
    abstract suspend fun getAllCategories(filter : String? = null,onlyWithActiveProducts : Boolean?=null): List<ProductCategoryEntity>


    @Transaction
    open suspend fun rebuild(products :List<ProductEntity>, categories: List<ProductCategoryEntity>) {
        deleteAll()
        deleteAllCategories()
        upsertCategories(categories)
        upsertAll(products)
    }
}