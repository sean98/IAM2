package com.iam_client.viewModels.main.customers

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.iam_client.repostories.customer.CustomerRepository
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.CustomerBalanceRecord
import com.iam_client.ui.utils.Formatter.formatMoneyString
import com.iam_client.viewModels.utils.ObservableViewModel
import com.iam_client.utills.reactive.Event
import kotlinx.coroutines.launch

class BalanceHistoryViewModel(
    private val customerRepository: CustomerRepository,
    private val initCustomer: Customer
) : ObservableViewModel() {
    val records: LiveData<PagedList<CustomerBalanceRecord>>
    val errorMsg: LiveData<Event<String>> = MutableLiveData()
    val balance: LiveData<String> = MutableLiveData()
    val showOpenRecordOnly = MutableLiveData<Boolean>()
    val refreshEvent : LiveData<Event<Boolean>> = MutableLiveData()

    init {
        //init balance from cached customer
        (balance as MutableLiveData).value = initCustomer.balance.formatMoneyString(initCustomer.currency)
        //on default show only non-balanced recordsLiveSource
        showOpenRecordOnly.value = true
        //when showOpenRecordOnly is changing -> update the records
        records = Transformations.switchMap(showOpenRecordOnly) {
            customerRepository.getCachedBalanceRecordsWithPagination(initCustomer, it)
        }
        refreshRecords()
        //update total balance when customer is changed (observe the repository)
        viewModelScope.launch {
            try {
                val customerLive = customerRepository.refreshCustomerByCid(initCustomer.cid!!)
                Transformations.map(customerLive) { it.balance }
                    .observeForever { balance.value = it.formatMoneyString(initCustomer.currency) }
            } catch (error: Throwable) {
                (errorMsg as MutableLiveData).value = Event("error ${error.message}")
            }
        }
    }

    fun refreshRecords() = viewModelScope.launch {
        //no need to reassign the recordsLiveSource because the PageList is db aware - so he wil update automatically
        (refreshEvent as MutableLiveData).value = Event(true)
        try {
            customerRepository.refreshBalanceRecordsCache(initCustomer)
        } catch (error: Throwable) {
            (errorMsg as MutableLiveData).value = Event("error ${error.message}")
        }
        finally {
            refreshEvent.value = Event(false)
        }
    }

}


class BalanceHistoryViewModelFactory(private val customerRepository: CustomerRepository) :
    ViewModelProvider.NewInstanceFactory() {
    lateinit var customer: Customer//a valid  cid (not null)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BalanceHistoryViewModel(customerRepository, customer) as T
    }
}
