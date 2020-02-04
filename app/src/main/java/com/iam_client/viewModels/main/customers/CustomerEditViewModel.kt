package com.iam_client.viewModels.main.customers

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import com.iam_client.R
import com.iam_client.repostories.customer.CustomerRepository
import com.iam_client.repostories.data.Address
import com.iam_client.repostories.data.CardGroup
import com.iam_client.repostories.data.Customer
import com.iam_client.repostories.data.Salesman
import com.iam_client.repostories.salesmen.SalesmenRepository
import com.iam_client.viewModels.utils.ObservableViewModel
import com.iam_client.utills.StringProvider
import com.iam_client.utills.reactive.EventWithReturn
import com.iam_client.utills.reactive.*
import com.iam_client.viewModels.exceptions.ProceesAreadyRunningException
import com.iam_client.viewModels.exceptions.SomeFieldsNotValidException
import kotlinx.coroutines.launch

class CustomerEditViewModel(
    private val customerRepository: CustomerRepository,
    private val salesmenRepository: SalesmenRepository,
    private val initCustomer: Customer,
    stringProvider: StringProvider
) : ObservableViewModel() {

    @Bindable
    val salesmanAdapter = { v: Any -> (v as Salesman).name }
    @Bindable
    val salesmenList: LiveData<List<Salesman>> = MutableLiveData()
    @Bindable
    val typeAdapter = { v: Any ->
        when (v as Customer.Type) {
            Customer.Type.Private -> stringProvider.getString(R.string.private_str)
            Customer.Type.Company ->  stringProvider.getString(R.string.company)
            Customer.Type.Employee ->  stringProvider.getString(R.string.employe)
        }
    }
    @Bindable
    val typeList = listOf(Customer.Type.Company, Customer.Type.Private)
    @Bindable
    val cardGroupAdapter = { v: Any -> (v as CardGroup).name }
    @Bindable
    val cardGroupList: LiveData<List<CardGroup>> = MutableLiveData()
    @Bindable
    val customerFields = CustomerBindable()
    val addressDialogNav: LiveData<EventWithReturn<Address?, Address>> = MutableLiveData()
    val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()
    val customerSavedEvent: LiveData<Event<Customer>> = MutableLiveData()
    val isSaving: LiveData<Boolean> = MutableLiveData()

    init {
        initCustomer()
        (isSaving as MutableLiveData).value = false
    }

    private fun initCustomer() = viewModelScope.launch {
        customerFields.fromCustomer(initCustomer)
        try {
            (salesmenList as MutableLiveData).value = salesmenRepository.getAll().filter { s -> s.isActive }
            (cardGroupList as MutableLiveData).value = customerRepository.getCardGroups()
        } catch (error: Throwable) {
            (errorMessage as MutableLiveData).value = Event(error)
        } finally {

        }
    }

    fun onShippingAddressClicked() {
        (addressDialogNav as MutableLiveData).value = EventWithReturn(initCustomer.shippingAddress) {
            initCustomer.shippingAddress = it
            customerFields.shippingAddressString.value = it.toStringFormat()
        }
    }

    fun onBillingAddressClicked() {
        (addressDialogNav as MutableLiveData).value = EventWithReturn(initCustomer.billingAddress) {
            initCustomer.billingAddress = it
            customerFields.billingAddressString.value = it.toStringFormat()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateCustomerProperties(customer: Customer): Boolean {
        //TODO error message on the text fields
        var isValid = true
        if (customer.name.isBlank())
            isValid = false
        if (!customer.email.isBlank() && !isEmailValid(customer.email))
            isValid = false
        return isValid
    }

    fun onSaveClicked() = viewModelScope.launch {
        if (isSaving.value != true) {
            (isSaving as MutableLiveData).value = true
            try {
                val customerToSave = customerFields.toCustomer(initCustomer.copy())
                if (validateCustomerProperties(customerToSave)) {
                    //if customer's Cid is null update the customer, else add as new customer
                    val savedCustomer: Customer =
                        if (!customerToSave.cid.isNullOrEmpty())
                            customerRepository.update(customerToSave)
                        else
                            customerRepository.add(customerToSave)
                    //notify the customer as been saved successfully
                    (customerSavedEvent as MutableLiveData).value = Event(savedCustomer)
                } else
                    throw SomeFieldsNotValidException()
            } catch (error: Throwable) {
                (errorMessage as MutableLiveData).value = Event(error)
            } finally {
                isSaving.value = false
            }
        } else {
            (errorMessage as MutableLiveData).value = Event(ProceesAreadyRunningException())
        }
    }




}

class CustomerBindable : Observable {
    @Bindable
    val cid = MutableLiveData<String>()
    @Bindable
    val name = MutableLiveData<String>()
    @Bindable
    val group = MutableLiveData<CardGroup>()
    @Bindable
    val type = MutableLiveData<Customer.Type>()
    @Bindable
    val federalTaxID = MutableLiveData<String>()
    @Bindable
    val phone1 = MutableLiveData<String>()
    @Bindable
    val phone2 = MutableLiveData<String>()
    @Bindable
    val cellular = MutableLiveData<String>()
    @Bindable
    val email = MutableLiveData<String>()
    @Bindable
    val fax = MutableLiveData<String>()
    @Bindable
    val salesman = MutableLiveData<Salesman>()
    @Bindable
    val comments = MutableLiveData<String>()
    @Bindable
    val shippingAddressString = MutableLiveData<String>()
    @Bindable
    val billingAddressString = MutableLiveData<String>()

    fun fromCustomer(customer: Customer?): CustomerBindable {

        cid.value = customer?.cid
        name.value = customer?.name
        federalTaxID.value = customer?.federalTaxID
        phone1.value = customer?.phone1
        phone2.value = customer?.phone2
        cellular.value = customer?.cellular
        email.value = customer?.email
        fax.value = customer?.fax
        comments.value = customer?.comments
        shippingAddressString.value = customer?.shippingAddress?.toStringFormat()
        billingAddressString.value = customer?.billingAddress?.toStringFormat()
        type.value = customer?.type
        if (customer?.group?.sn != Int.MIN_VALUE)
            group.value = customer?.group
        if (customer?.salesman?.sn != Int.MIN_VALUE)
            salesman.value = customer?.salesman
        return this
    }

    fun toCustomer(baseCustomer: Customer): Customer {
        baseCustomer.let {
            it.name = name.value ?: ""
            it.federalTaxID = federalTaxID.value ?: ""
            it.cellular = cellular.value ?: ""
            it.comments = comments.value ?: ""
            it.email = email.value ?: ""
            it.fax = fax.value ?: ""
            it.phone1 = phone1.value ?: ""
            it.phone2 = phone2.value ?: ""
            it.group = group.value!!
            it.type = type.value!!
            it.salesman = salesman.value!!
            //address updated directly on initCustomer
        }
        return baseCustomer
    }

    @Transient
    private var mCallbacks: PropertyChangeRegistry? = null

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            if (mCallbacks == null) mCallbacks = PropertyChangeRegistry()
        }
        mCallbacks!!.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            if (mCallbacks == null) return
        }
        mCallbacks!!.remove(callback)
    }
}


class CustomerCardEditModeModelFactory(
    private val customerRepository: CustomerRepository,
    private val salesmenRepository: SalesmenRepository,
    private val stringProvider: StringProvider
) : ViewModelProvider.NewInstanceFactory() {

    lateinit var customer: Customer

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CustomerEditViewModel(customerRepository, salesmenRepository, customer,stringProvider) as T
    }
}

