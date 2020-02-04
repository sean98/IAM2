package com.iam_client.viewModels.main.customers

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iam_client.repostories.data.Address
import com.iam_client.repostories.data.Customer
import com.iam_client.viewModels.utils.ObservableViewModel
import com.iam_client.utills.reactive.Event
import com.iam_client.viewModels.exceptions.SomeFieldsNotValidException

class SetAddressSharedViewModel : ObservableViewModel() {
    //will publish a valid address -> dialog dismiss and caller gain the result
    var confirmedAddress: LiveData<Address> = MutableLiveData()

    @Bindable
    val addressType = MutableLiveData<String>()
    @Bindable
    val city = MutableLiveData<String>()
    @Bindable
    val street = MutableLiveData<String>()
    @Bindable
    val streetNum = MutableLiveData<String>()
    @Bindable
    val apartment = MutableLiveData<String>()
    @Bindable
    val block = MutableLiveData<String>()
    @Bindable
    val zipcode = MutableLiveData<String>()

    val errorFields :LiveData<Set<ErrorField>> = MutableLiveData()
    val isAddressTypeEnabled: LiveData<Boolean> = MutableLiveData()
    val errorMessage : LiveData<Event<Throwable>> = MutableLiveData()

    fun onConfirmClicked() {
        //publish changed to subscribers
        val address =  Address(
            addressType.value,
            "IL",
            city.value ?: "",
            block.value ?: "",
            street.value ?: "",
            streetNum.value ?: "",
            apartment.value ?: "",
            zipcode.value ?: ""
        )
        if(validateAddress(address)){
            (confirmedAddress as MutableLiveData).value =address
            //newInstance new observer that will not notify previous subscribers
            confirmedAddress = MutableLiveData()
        }
        else {
            (errorMessage as MutableLiveData).value = Event(SomeFieldsNotValidException())
        }

    }

    fun initAddress(address: Address?) {
        (isAddressTypeEnabled as MutableLiveData).value = address == null || address.id.isNullOrEmpty()
        addressType.value = address?.id
        city.value = address?.city
        street.value = address?.street
        streetNum.value = address?.numAtStreet
        apartment.value = address?.apartment
        block.value = address?.block
        zipcode.value = address?.zipCode
        if(address!=null)
            validateAddress(address)
    }

    private fun validateAddress(address: Address): Boolean = address.run {
        val errors = mutableSetOf<ErrorField>()
        if(id.isNullOrBlank())
            errors.add(ErrorField.Name)
        if(city.isNullOrBlank())
            errors.add(ErrorField.City)
        (errorFields as MutableLiveData).value = errors
        return errors.isEmpty()
    }

    enum class ErrorField {Name,City}
}


class SetAddressSharedViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    var customer: Customer? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SetAddressSharedViewModel() as T
    }
}
