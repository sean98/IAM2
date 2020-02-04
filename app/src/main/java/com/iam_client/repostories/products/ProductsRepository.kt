package com.iam_client.repostories.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.iam_client.local.dao.TranslationDao
import com.iam_client.local.dao.products.ProductsDao
import com.iam_client.local.data.Translation
import com.iam_client.remote.apiServices.products.ProductsApiService
import com.iam_client.repostories.data.products.Product
import com.iam_client.repostories.data.products.ProductCategory
import com.iam_client.repostories.data.products.ProductProperty
import com.iam_client.remote.apiServices.googleApi.ITranslateService
import com.iam_client.repostories.utils.mappers.mapToEntity
import com.iam_client.repostories.utils.mappers.mapToModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProductsRepository(
    private val productsDao: ProductsDao,
    private val productsApiService: ProductsApiService,
    private val translateService: ITranslateService,
    private val translationDao: TranslationDao

) : IProductRepository {


    override fun getProductCategoriesWithPagination(
        filter: String?,
        onlyWithActiveProducts: Boolean?
    ): LiveData<PagedList<ProductCategory>> {
        val factory = productsDao.getAllCategoriesWithPaging(filter, onlyWithActiveProducts)
            .map { it.mapToModel() }
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(10) //.setPrefetchDistance(1)
            .build()

        return LivePagedListBuilder(factory, config)
            .build()
    }

    override fun getProductsWithPagination(
        filter: String?,
        category: ProductCategory?,
        activeOnly: Boolean?
    ): LiveData<PagedList<Product>> {
        val factory = productsDao.getAllProductsWithPaging(filter, category?.code, activeOnly)
            .map { it.mapToModel() }
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(10) //.setPrefetchDistance(1)
            .build()

        return LivePagedListBuilder(factory, config)
            .build()
    }

    override suspend fun refreshProductsAndCategories() = withContext(Dispatchers.IO) {
        val productsLastUpdateDateTime = productsDao.getLastUpdatedProductTime()
        val categoriesLastUpdateDateTime = productsDao.getLastUpdatedCategoryTime()


        val categories = productsApiService.getAllProductCategories(categoriesLastUpdateDateTime)
            .map { it.mapToEntity() }
        productsDao.upsertCategories(categories)
        val products =
            productsApiService.getAllProducts(productsLastUpdateDateTime).map { it.mapToEntity() }
        productsDao.upsertAll(products)
    }

    override suspend fun getProduct(code: String): Product = withContext(Dispatchers.IO) {
        var cachedProduct = productsDao.getProduct(code)
        if (cachedProduct == null) {
            val apiProduct = productsApiService.getProduct(code)
            cachedProduct = apiProduct.mapToEntity()
            productsDao.upsert(cachedProduct)
        }
        return@withContext cachedProduct.mapToModel()
    }


    override fun getPropertiesLocalName(properties: List<ProductProperty>): LiveData<Map<ProductProperty, String?>> {
        val langCode = Locale.getDefault().language
        GlobalScope.launch {
            properties.forEach {
                if (!translationDao.isTranslationExist(it.code,langCode)) {
                    val localName = translateService.translateToLocal(it.code)
                    if (localName != null)
                        translationDao.upsert(Translation(it.code,langCode,localName))
                }
            }
        }
        val transArray =
            translationDao.getTranslationLive(properties.map { it.code }.toTypedArray(), langCode)
        return  Transformations.map(transArray){
            it.map { x->properties.first{y->y.code == x.source} to x.translatedText }.toMap()
        }



    }


    //Singleton
    companion object {
        @Volatile
        private var instance: ProductsRepository? = null

        fun getInstance(
            productsDao: ProductsDao,
            productsApiService: ProductsApiService,
            translateService: ITranslateService,
            translationDao: TranslationDao
        ) =
            instance ?: synchronized(this) {
                instance ?: ProductsRepository(
                    productsDao,
                    productsApiService,
                    translateService,
                    translationDao
                )
                    .also { instance = it }
            }
    }
}