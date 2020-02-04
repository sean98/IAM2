package com.iam_client.viewModels.main.customers

import androidx.lifecycle.*
import com.iam_client.repostories.customer.CustomerRepository
import com.iam_client.repostories.data.Customer
import com.iam_client.viewModels.utils.ObservableViewModel
import com.iam_client.utills.reactive.Event
import kotlinx.coroutines.launch


class CustomerCardViewModel(
    private val customerRepository: CustomerRepository,
    private val initCustomerCid: String
) : ObservableViewModel() {
    val customer: LiveData<Customer> = customerRepository.getCachedCustomerById(initCustomerCid)
    val errorMsg: LiveData<Event<String>> = MutableLiveData()
    val updatedNotification: LiveData<Event<Boolean>> = MutableLiveData()
    val refreshing: LiveData<Event<Boolean>> = MutableLiveData()
    val editCustomerNav: LiveData<Event<Customer>> = MutableLiveData()
    val balanceHistoryNav: LiveData<Event<Customer>> = MutableLiveData()
    val documentsNav: LiveData<Event<Customer>> = MutableLiveData()
    val createDocumentsNav: LiveData<Event<Customer>> = MutableLiveData()

    init {
        refresh()//refresh online the cached customer
    }

    fun refresh() {
        (refreshing as MutableLiveData).value = Event(true)
        viewModelScope.launch {
            try{
                customerRepository.refreshCustomerByCid(initCustomerCid)
                (updatedNotification as MutableLiveData).value = Event(true)
            }catch (error: Throwable){
                (updatedNotification as MutableLiveData).value = Event(false)
                (errorMsg as MutableLiveData).value = Event("error: ${error.message}")
            }
            finally {
                refreshing.value = Event(false)
            }
        }
    }

    fun onEditClicked() {
        val c = customer.value
        if (refreshing.value?.peekContent() == true)
            (errorMsg as MutableLiveData).value = Event("cant edit customer until refresh is finished")//TODO strings
        else if (c != null) {
            (editCustomerNav as MutableLiveData).value = Event(c)
        }
    }

    fun onBalanceHistoryClicked() {
        val c = customer.value
        if (c != null) {
            (balanceHistoryNav as MutableLiveData).value = Event(c)
        }
    }

    fun onDocumentNav() {
        val c = customer.value
        if (c != null) {
            (documentsNav as MutableLiveData).value = Event(c)
        }
    }

    fun onCreateDocumentNav() {
        val c = customer.value
        if (c != null) {
            (createDocumentsNav as MutableLiveData).value = Event(c)
        }
    }
}


class CustomerCardViewModelFactory(
    private val customerRepository: CustomerRepository
) : ViewModelProvider.NewInstanceFactory() {
    lateinit var customerCid: String//a valid  cid (not null)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CustomerCardViewModel(customerRepository, customerCid) as T
    }
}
