package com.iam_client.viewModels.main.salesman

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.iam_client.repostories.data.Employee
import com.iam_client.repostories.data.Salesman
import com.iam_client.repostories.data.docs.Document
import com.iam_client.repostories.data.docs.Invoice
import com.iam_client.repostories.data.docs.Order
import com.iam_client.repostories.data.docs.Quotation
import com.iam_client.repostories.documents.DocumentRepository
import com.iam_client.repostories.employee.IEmployeeRepository
import com.iam_client.repostories.salesmen.SalesmenRepository
import com.iam_client.repostories.user.IUserRepository
import com.iam_client.utills.reactive.Event
import com.iam_client.viewModels.main.documents.IDocumentListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

class SalesmanDocumentsViewModel(
    private val invoicesRepository: DocumentRepository<Invoice>,
    private val quotationsRepository: DocumentRepository<Quotation>,
    private val orderRepository: DocumentRepository<Order>,
    private val salesmenRepository: SalesmenRepository,
    private val employeeRepository: IEmployeeRepository,
    private val userRepository: IUserRepository

) : ViewModel() {

    private lateinit var quotationsListViewModel: IDocumentListViewModel
    private lateinit var invoicesListViewModel: IDocumentListViewModel
    private lateinit var ordersListViewModel: IDocumentListViewModel

    var isAuthorized = false
        private set

    lateinit var salesman: Salesman

    init {
        runBlocking(Dispatchers.IO) {
            //TODO try catch
            val employeeSn = userRepository.getLoggedInUserLive().value?.employeeSN

            val employee: Employee? =
                if (employeeSn != null)
                    employeeRepository.getEmployee(employeeSn)
                else null

            if (employee?.salesmanSN != null){
                isAuthorized = true
                salesman = salesmenRepository.getSalesman(employee.salesmanSN)

                quotationsListViewModel = DocumentListViewModel(
                    quotationsRepository,
                    viewModelScope,
                    salesman,
                    Quotation::class
                )
                invoicesListViewModel = DocumentListViewModel(
                    invoicesRepository,
                    viewModelScope,
                    salesman,
                    Invoice::class
                )
                ordersListViewModel =
                    DocumentListViewModel(orderRepository, viewModelScope, salesman, Order::class)
            }

        }
    }

    fun getQuotationsListViewModel() = quotationsListViewModel

    fun getOrdersListViewModel() = ordersListViewModel

    fun getInvoicesListViewModel() = invoicesListViewModel


    class DocumentListViewModel<TDoc : Document>(
        private val documentRepository: DocumentRepository<TDoc>,
        private val viewModelScope: CoroutineScope,
        private val salesman: Salesman,
        override val type: KClass<TDoc>
    ) : IDocumentListViewModel {
        override val refreshEvent: LiveData<Event<Boolean>> = MutableLiveData()
        override val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()

        override fun getDocumentList(): LiveData<PagedList<Document>> =
            documentRepository.getAllBySalesmanWithPagination(
                salesman,
                true
            ) as LiveData<PagedList<Document>>


        override fun refresh() {
            viewModelScope.launch {
                (refreshEvent as MutableLiveData).value = Event(true)
                try {
                    documentRepository.refreshAllBySalesmen(salesman, true)

                } catch (error: Throwable) {
                    (errorMessage as MutableLiveData).value = Event(error)
                } finally {
                    refreshEvent.value = Event(false)
                }
            }
        }
    }
}


class SalesmanDocumentsViewModelFactory(
    private val invoicesRepository: DocumentRepository<Invoice>,
    private val quotationsRepository: DocumentRepository<Quotation>,
    private val orderRepository: DocumentRepository<Order>,
    private val salesmenRepository: SalesmenRepository,
    private val employeeRepository: IEmployeeRepository,
    private val userRepository: IUserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SalesmanDocumentsViewModel(
            invoicesRepository,
            quotationsRepository,
            orderRepository,
            salesmenRepository,
            employeeRepository,
            userRepository
        ) as T
    }
}
