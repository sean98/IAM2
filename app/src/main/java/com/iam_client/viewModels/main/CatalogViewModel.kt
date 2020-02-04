package com.iam_client.viewModels.main

import androidx.lifecycle.*
import com.iam_client.repostories.data.products.ProductCategory
import com.iam_client.repostories.products.IProductRepository
import com.iam_client.repostories.products.ProductsRepository
import com.iam_client.utills.reactive.Event
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: IProductRepository) : ViewModel() {
    init {
        refresh()
    }
    val refreshEvent : LiveData<Event<Boolean>> = MutableLiveData()

    val errorMessage : LiveData<Event<Throwable>> = MutableLiveData()

    fun getProducts(filter: String? = null, category: ProductCategory?=null) =
        repository.getProductsWithPagination(filter, category,true)

    fun getCategories(filter: String? = null) =
        repository.getProductCategoriesWithPagination(filter,true)


    fun refresh() = viewModelScope.launch  {
        (refreshEvent as MutableLiveData).value = Event(true)
        try{
            repository.refreshProductsAndCategories()
        }catch (error:Throwable){
            (errorMessage as MutableLiveData).value = Event(error)
        }
        finally {
            refreshEvent.value = Event(false)
        }

    }
}


class CatalogViewModelFactory(private val repository: IProductRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CatalogViewModel(repository) as T
}