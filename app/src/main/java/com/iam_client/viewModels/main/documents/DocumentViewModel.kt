package com.iam_client.viewModels.main.documents

import androidx.lifecycle.*
import com.iam_client.repostories.data.Salesman
import com.iam_client.repostories.data.docs.*
import com.iam_client.repostories.documents.DocumentRepository
import com.iam_client.repostories.documents.impl.QuotationRepository
import com.iam_client.repostories.salesmen.SalesmenRepository
import com.iam_client.utills.reactive.Event
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import kotlin.reflect.KClass

class DocumentViewModel<TDoc>(
    private val repository: DocumentRepository<TDoc>,
    private val salesmenRepository: SalesmenRepository,
    private val docSn: Int,
    private var docType: KClass<out Document>
) : ViewModel() where TDoc : Document {

    val salesman: LiveData<Salesman>
    val document: LiveData<TDoc> = repository.getCachedBySn(docSn)
    val refreshing: LiveData<Boolean> = MutableLiveData()
    val itemLoading: LiveData<Boolean> = MutableLiveData()

    val errorEvent: LiveData<Event<Throwable>> = MutableLiveData()
    val isEditable: LiveData<Boolean> = Transformations.map(document) {
        (it != null && !it.isCanceled && !it.isClosed)
    }
    val isCancelable: LiveData<Boolean> = Transformations.map(document) {
        (it != null && !it.isCanceled && !it.isClosed && (docType ==  Quotation::class /*|| docType ==Order::class*/))
    }
    val editDocumentEvent :LiveData<Event<Document>> = MutableLiveData()

    init {
        (refreshing as MutableLiveData).value = false
        refresh()
        salesman = Transformations.switchMap(document) {
            if (it?.salesmanSN != null)
                salesmenRepository.getCachedSalesmanBySNLive(it.salesmanSN!!)
            else MutableLiveData()
        }
    }


    fun refresh() = viewModelScope.launch {

        val isLoadItem = document.value?.items == null
        if(isLoadItem)
            (itemLoading as MutableLiveData).value = true
        else
            (refreshing as MutableLiveData).value = true
        try {
            repository.refreshBySn(docSn)
        } catch (error: Throwable) {
            (errorEvent as MutableLiveData).value = Event(error)
        } finally {
            if(isLoadItem)
                (itemLoading as MutableLiveData).value = false
            else
                (refreshing as MutableLiveData).value = false
        }
    }

    fun cancelDocument() = viewModelScope.launch {
        (refreshing as MutableLiveData).value = true
        try {
            val rep: QuotationRepository? = when(docType){
                Quotation::class ->repository as QuotationRepository
                else -> null
            } ?: throw RuntimeException("Not Implemented yet (${docType.simpleName}) TODO!!")

            rep?.cancelDoc(document.value!! as Quotation)

        } catch (error: Throwable) {
            (errorEvent as MutableLiveData).value = Event(error)
        } finally {
            refreshing.value = false
        }
    }

    fun editDocument(){
        if(isEditable.value?:false){
            when(docType){
                Quotation::class ->(editDocumentEvent as MutableLiveData).value = Event(document.value!!)
                else -> (errorEvent as MutableLiveData).value = Event(RuntimeException("Not Implemented yet ${docType.simpleName} TODO!!"))
            }
        }
        else{
            (errorEvent as MutableLiveData).value = Event(RuntimeException("Document is not editable"))

        }


    }


}

open class DocumentViewModelFactory<TDoc>(
    private val repository: DocumentRepository<TDoc>,
    private val salesmenRepository: SalesmenRepository

) : ViewModelProvider.NewInstanceFactory() where TDoc : Document {
    var docSn: Int? = null
    lateinit var docType: KClass<out Document>

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DocumentViewModel(repository, salesmenRepository, docSn!!, docType) as T
    }
}


class InvoiceViewModelFactory(
    repository: DocumentRepository<Invoice>,
    salesmenRepository: SalesmenRepository
) :
    DocumentViewModelFactory<Invoice>(repository, salesmenRepository)

class CreditNoteViewModelFactory(
    repository: DocumentRepository<CreditNote>,
    salesmenRepository: SalesmenRepository
) :
    DocumentViewModelFactory<CreditNote>(repository, salesmenRepository)

class OrderViewModelFactory(
    repository: DocumentRepository<Order>,
    salesmenRepository: SalesmenRepository
) :
    DocumentViewModelFactory<Order>(repository, salesmenRepository)

class QuotationViewModelFactory(
    repository: DocumentRepository<Quotation>,
    salesmenRepository: SalesmenRepository
) :
    DocumentViewModelFactory<Quotation>(repository, salesmenRepository)

class DeliveryNoteViewModelFactory(
    repository: DocumentRepository<DeliveryNote>,
    salesmenRepository: SalesmenRepository
) :
    DocumentViewModelFactory<DeliveryNote>(repository, salesmenRepository)