//package com.iam_client.viewModels.main.documents
//
//import androidx.lifecycle.*
//import com.iam_client.repostories.data.Customer
//import com.iam_client.repostories.data.docs.*
//import com.iam_client.repostories.documents.DocumentRepository
//import com.iam_client.utills.reactive.Event
//import kotlinx.coroutines.launch
//
//class CustomerDocumentListViewModel<TDoc : Document>(
//    private val repository: DocumentRepository<TDoc>
//) : ViewModel(), IDocumentListViewModel<TDoc> {
//
//    private lateinit var customer: Customer
//    fun setCustomer(customer: Customer) {
//        this.customer = customer
//    }
//
//    override val refreshEvent: LiveData<Event<Boolean>> = MutableLiveData()
//    override val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()
//
//    override fun getDocumentList() =
//        repository.getAllByCustomerWithPagination(customer)
//
//    override fun refreshList() {
//        viewModelScope.launch {
//            (refreshEvent as MutableLiveData).value = Event(true)
//            try {
//                repository.refreshAllByCustomer(customer)
//
//            } catch (error: Throwable) {
//                (errorMessage as MutableLiveData).value = Event(error)
//            } finally {
//                refreshEvent.value = Event(false)
//            }
//        }
//    }
//}
//
//@Suppress("UNCHECKED_CAST")
//open class DocumentListViewModelFactory<TDoc : Document>(
//    private val repository: DocumentRepository<TDoc>
//) : ViewModelProvider.NewInstanceFactory() {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return CustomerDocumentListViewModel(repository) as T
//    }
//}
//
//class InvoiceListViewModelFactory(repository: DocumentRepository<Invoice>) :
//    DocumentListViewModelFactory<Invoice>(repository)
//
//class OrderListViewModelFactory(repository: DocumentRepository<Order>) : DocumentListViewModelFactory<Order>(repository)
//class DeliveryListViewModelFactory(repository: DocumentRepository<DeliveryNote>) :
//    DocumentListViewModelFactory<DeliveryNote>(repository)
//
//class QuotationListViewModelFactory(repository: DocumentRepository<Quotation>) :
//    DocumentListViewModelFactory<Quotation>(repository)
//
//class CreditListViewModelFactory(repository: DocumentRepository<CreditNote>) :
//    DocumentListViewModelFactory<CreditNote>(repository)