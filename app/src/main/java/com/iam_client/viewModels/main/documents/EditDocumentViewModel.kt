package com.iam_client.viewModels.main.documents

import androidx.databinding.Bindable
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.Salesman
import com.iam_client.repostories.data.docs.DocItem
import com.iam_client.repostories.data.docs.Document
import com.iam_client.repostories.data.docs.Order
import com.iam_client.repostories.data.docs.Quotation
import com.iam_client.repostories.data.products.Product
import com.iam_client.repostories.documents.DocumentRepository
import com.iam_client.repostories.salesmen.SalesmenRepository
import com.iam_client.viewModels.utils.ObservableViewModel
import com.iam_client.utills.reactive.Event
import com.iam_client.utills.reactive.collectionsLiveData.MutableListLiveData
import com.iam_client.utills.reactive.observeOnce
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.util.*
import kotlin.reflect.KClass

//TODO - Generic ViewModel
class EditDocumentViewModel(
    private val documentRepository: DocumentRepository<Document>,
    private val salesmenRepository: SalesmenRepository,
    documentToEdit: Int?,//null if new
    private val customer: Customer?,//null if editing
    private val docType: KClass<out Document>
) : ObservableViewModel() {

    @Bindable
    val comments = MutableLiveData<String>()

    val header: LiveData<Document> = MutableLiveData()

    val items = MutableListLiveData<DocItem>()
    @Bindable
    val salesmanAdapter = { v: Any -> (v as Salesman).name }
    @Bindable
    val salesmenList: LiveData<List<Salesman>> = MutableLiveData()
    @Bindable
    val salesman = MutableLiveData<Salesman>()

    val openDocItemEditDialog: LiveData<Event<DocItem>> = MutableLiveData()

    val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()

    val loading: LiveData<Boolean> = MutableLiveData()

    val docSaved : LiveData<Event<Document>> = MutableLiveData()


    init {
        if (documentToEdit == null) {
            //create new document
            (header as MutableLiveData).value = when (docType) {
                Quotation::class -> Quotation(null)
                Order::class -> Order(null)
                else -> throw RuntimeException("Edit doc ${docType.simpleName} not implemented!!!")
            }.apply {
                if (customer != null) {
                    customerName = customer.name
                    customerSN = customer.cid!!
                    customerAddress = customer.billingAddress?.toStringFormat() ?: ""
                    customerFederalTaxID = customerFederalTaxID ?: ""
                    items = mutableListOf()
                    vatPercent = 17.0//TODO get default vat from ERP,
                    currency = customer.currency//always use customer's default currency
                    date = Date()
                }
            }
        } else {
            //edit exist document
            documentRepository.getCachedBySn(documentToEdit).observeOnce(Observer {
                (header as MutableLiveData).value = it
                items.addAll(it.items ?: listOf())
                if (it.items == null) {
                    TODO("Handle null items - get from web api")
                }
            })


        }

        //get list of salesmen for Spinner
        viewModelScope.launch {
            try {
                (loading as MutableLiveData).value = true
                val list = salesmenRepository.getAll()
                (salesmenList as MutableLiveData).value = list.filter { it.isActive }
                //if a salesman is attached to the document- select the salesman accordingly
                if (header.value?.salesmanSN != null)
                    salesman.value = list.first { s -> s.sn == header.value?.salesmanSN }
            } catch (error: Throwable) {
                (errorMessage as MutableLiveData).value = Event(error)
            } finally {
                (loading as MutableLiveData).value = false
            }
        }
        //when items changed - update the header values
        items.observeForever { reCalcHeader() }
    }

    fun addNewItem(docItem: DocItem) {
        items.add(docItem)
    }


    private fun reCalcHeader() {
        val doc = header.value
        if (doc != null) {
            //recalculate the properties when the items changed
            val docTotalBeforeVatAndDiscount = items.getList().sumByDouble { itm -> itm.totalPrice }
            val vat = (doc.vatPercent ?: 0.0) / 100 * docTotalBeforeVatAndDiscount
            val discount = (doc.discountPercent ?: 0.0 / 100) * docTotalBeforeVatAndDiscount
            doc.vat = vat
            doc.docTotal = docTotalBeforeVatAndDiscount + vat + discount
            (header as MutableLiveData).value = header.value  //notify value changed
        }
    }

    fun addNewItem(product: Product) {
        val item = DocItem(
            code = product.code,
            docNumber = header.value?.sn,
            itemNumber = null,//new item
            description = product.name,
            details = null,
            comments = null,
            discountPercent = 0.0,
            isOpen = true,
            quantity = 0.0,
            openQuantity = null,
            visualOrder = items.getList().size,
            pricePerQuantity = 0.0,
            currency = header.value?.currency,//always use document currency
            properties = mutableMapOf(),
            followDoc = null,
            baseItemNumber = null,
            baseDoc = null
        )
        (openDocItemEditDialog as MutableLiveData).value = Event(item)
    }

    fun onSaveClicked() {
        val docToEdit = header.value
        val itemsToAdd = items.getList()
            .union(header.value?.items?.filter {
                items.getList().all { x -> x.itemNumber != it.itemNumber }
            }?.map { it.apply { quantity = 0.0 } }.orEmpty()).toList()

        if (docToEdit != null) {
            docToEdit.apply {
                items = itemsToAdd
                salesmanSN = salesman.value?.sn
                comments = this@EditDocumentViewModel.comments.value
            }
            //TODO validate before Add
            viewModelScope.launch {
                (loading as MutableLiveData).value = true
                try {
                    val editedDoc =
                        if (docToEdit.sn == null)
                            documentRepository.addNew(docToEdit)
                        else
                            documentRepository.update(docToEdit)
                    (docSaved as MutableLiveData).value = Event(editedDoc)
                } catch (error: Throwable) {
                    (errorMessage as MutableLiveData).value = Event(error)
                } finally {
                    loading.value = false
                }

            }
        }
    }


}


class EditDocumentModeModelFactory(
    private val repositoryQuotation: DocumentRepository<Quotation>,
    private val repositoryOrder: DocumentRepository<Order>,
    private val salesmenRepository: SalesmenRepository
) : ViewModelProvider.NewInstanceFactory() {

    var customer: Customer? = null
    var docSN: Int? = null
    lateinit var docType: KClass<out Document>
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val rep = when (docType) {
            Quotation::class -> repositoryQuotation
            Order::class -> repositoryOrder
            else -> throw RuntimeException("Edit doc ${docType.simpleName} not implemented!!!")
        } as DocumentRepository<Document>
        return EditDocumentViewModel(rep, salesmenRepository, docSN, customer, docType) as T
    }
}