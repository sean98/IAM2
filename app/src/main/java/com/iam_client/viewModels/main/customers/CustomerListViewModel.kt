package com.iam_client.viewModels.main.customers

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.customer.CustomerRepository
import com.iam_client.utills.reactive.Event
import kotlinx.coroutines.launch

/*
https://medium.com/androiddevelopers/livedata-beyond-the-viewmodel-reactive-patterns-using-transformations-and-mediatorlivedata-fda520ba00b7
*/
class CustomerListViewModel(private val customerRepository: CustomerRepository) : ViewModel() {

    val filteredText : LiveData<String> =  MutableLiveData()

    val customers: LiveData<PagedList<Customer>>

    val addCustomerEvent :LiveData<Event<Unit>> = MutableLiveData()
    val refreshEvent :LiveData<Event<Boolean>> = MutableLiveData()

    val errorMessage :LiveData<Event<String>> = MutableLiveData()

    init {
        customers = Transformations.switchMap(filteredText) {
            customerRepository.getFilteredCustomersWithPagination(it)
        }
        (filteredText as MutableLiveData).value = ""

        refreshCustomers()
    }

    fun filterCustomers(filter: String) {
        (filteredText as MutableLiveData).value = filter
    }

    fun refreshCustomers() = viewModelScope.launch  {
        (refreshEvent as MutableLiveData).value = Event(true)
        try{
            customerRepository.refreshCustomers()
            customers.value?.dataSource?.invalidate()
        }catch (error:Throwable){
            (errorMessage as MutableLiveData).value = Event("error: ${error.message}")
        }
        finally {
            refreshEvent.value = Event(false)
        }

    }

    fun onAddCustomerClicked(){
        (addCustomerEvent as MutableLiveData).value = Event(Unit)
    }

}
