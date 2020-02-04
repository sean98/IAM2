package com.iam_client.viewModels.main.documents

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.docs.*
import com.iam_client.repostories.documents.DocumentRepository
import com.iam_client.utills.reactive.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class CustomerDocumentsTabsViewModel(
    private val invoicesRepository: DocumentRepository<Invoice>,
    private val quotationsRepository: DocumentRepository<Quotation>,
    private val orderRepository: DocumentRepository<Order>,
    private val creditNotesRepository: DocumentRepository<CreditNote>,
    private val deliveryNotesRepository: DocumentRepository<DeliveryNote>,
    private val customer: Customer


) : ViewModel() {

    private lateinit var quotationsListViewModel: IDocumentListViewModel
    private lateinit var invoicesListViewModel: IDocumentListViewModel
    private lateinit var ordersListViewModel: IDocumentListViewModel
    private lateinit var deliveryNoteListViewModel: IDocumentListViewModel
    private lateinit var creditNotesListViewModel: IDocumentListViewModel


    init {

        quotationsListViewModel = DocumentListViewModel(
            viewModelScope,
            Quotation::class,
            quotationsRepository,
            customer
        )
        ordersListViewModel = DocumentListViewModel(
            viewModelScope,
            Order::class,
            orderRepository,
            customer
        )
        deliveryNoteListViewModel = DocumentListViewModel(
            viewModelScope,
            DeliveryNote::class,
            deliveryNotesRepository,
            customer
        )
        invoicesListViewModel = DocumentListViewModel(
            viewModelScope,
            Invoice::class,
            invoicesRepository,
            customer
        )
        creditNotesListViewModel = DocumentListViewModel(
            viewModelScope,
            CreditNote::class,
            creditNotesRepository,
            customer
        )
    }

    fun getQuotationsListViewModel() = quotationsListViewModel

    fun getOrdersListViewModel() = ordersListViewModel

    fun getInvoicesListViewModel() = invoicesListViewModel

    fun getCreditNotesListViewModel() = creditNotesListViewModel

    fun getDeliveryNoteListViewModel() = deliveryNoteListViewModel


    class DocumentListViewModel<TDoc : Document>(
        private val viewModelScope: CoroutineScope,
        override val type: KClass<TDoc>,
        private val documentRepository: DocumentRepository<TDoc>,
        private val customer: Customer

    ) : IDocumentListViewModel {
        override val refreshEvent: LiveData<Event<Boolean>> = MutableLiveData()
        override val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()

        override fun getDocumentList(): LiveData<PagedList<Document>> =
            documentRepository.getAllByCustomerWithPagination(customer) as LiveData<PagedList<Document>>


        override fun refresh() {
            viewModelScope.launch {
                (refreshEvent as MutableLiveData).value = Event(true)
                try {
                    documentRepository.refreshAllByCustomer(customer)
                } catch (error: Throwable) {
                    (errorMessage as MutableLiveData).value = Event(error)
                } finally {
                    refreshEvent.value = Event(false)
                }
            }

        }
    }


}

class CustomerDocumentsTabsViewModelFactory(
    private val invoicesRepository: DocumentRepository<Invoice>,
    private val quotationsRepository: DocumentRepository<Quotation>,
    private val orderRepository: DocumentRepository<Order>,
    private val creditNotesRepository: DocumentRepository<CreditNote>,
    private val deliveryNotesRepository: DocumentRepository<DeliveryNote>
) : ViewModelProvider.NewInstanceFactory() {

    lateinit var customer: Customer
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CustomerDocumentsTabsViewModel(
            invoicesRepository,
            quotationsRepository,
            orderRepository,
            creditNotesRepository,
            deliveryNotesRepository,
            customer
        ) as T
    }
}