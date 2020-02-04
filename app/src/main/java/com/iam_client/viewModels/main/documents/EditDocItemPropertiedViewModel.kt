package com.iam_client.viewModels.main.documents

import androidx.databinding.Bindable
import androidx.databinding.ObservableDouble
import androidx.lifecycle.*
import com.iam_client.repostories.data.docs.DocItem
import com.iam_client.repostories.data.products.ProductProperty
import com.iam_client.repostories.products.IProductRepository
import com.iam_client.viewModels.utils.ObservableViewModel
import com.iam_client.utills.reactive.Event
import com.iam_client.utills.reactive.addOnPropertyChanged
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mariuszgromada.math.mxparser.*

class EditDocItemPropertiedViewModel(
    private val productRepository: IProductRepository,
    private val docItem: DocItem
) : ObservableViewModel() {
    @Bindable
    val itemCode: LiveData<String> = MutableLiveData()
    @Bindable
    val descriptionEditable = MutableLiveData<String>()
    @Bindable
    val quantityEditable = MutableLiveData<String>()
    @Bindable
    val pricePerQuantityEditable = MutableLiveData<String>()
    @Bindable
    val discountPercentEditable = MutableLiveData<String>()
    @Bindable
    val detailsEditable = MutableLiveData<String>()
    @Bindable
    val commentsEditable = MutableLiveData<String>()
    @Bindable
    val totalPriceEditable = MutableLiveData<String>()
    @Bindable
    val isQuantityEditable: LiveData<Boolean> = MutableLiveData()

    val properties: LiveData<List<ProductProperty>> = MutableLiveData()

    val errorMessage: LiveData<Event<Throwable>> = MutableLiveData()

    val confirmEvent: LiveData<Event<DocItem>> = MutableLiveData()

    val loading: LiveData<Boolean> = MutableLiveData()

    val currency = docItem.currency

    val propertiesLocalName: LiveData<Map<ProductProperty, String?>> =
        Transformations.switchMap(properties) {
            productRepository.getPropertiesLocalName(it)
        }

    private val propertiesValues = mutableMapOf<String, Any>()

    private var quantityCalculator: Expression? = null

    private val calculatedQuantity = ObservableDouble()

    private var isUserEditTotalPrice: Boolean = false

    private val nonDoubleRegex = Regex("[^-+0-9.]")

    init {
        //init field according to the given docItem
        calculatedQuantity.set(docItem.quantity)
        docItem.apply {
            (itemCode as MutableLiveData).value = code
            descriptionEditable.value = description
            quantityEditable.value = "$quantity"
            pricePerQuantityEditable.value = "$pricePerQuantity"
            discountPercentEditable.value = "$discountPercent"
            detailsEditable.value = details ?: ""
            commentsEditable.value = comments
            propertiesValues.putAll(properties)
        }
        calculatedQuantity.addOnPropertyChanged { refreshTotalPrice() }
        //observe user inputs and auto update field accordingly
        pricePerQuantityEditable.observeForever { refreshTotalPrice() }
        discountPercentEditable.observeForever { refreshTotalPrice() }
        totalPriceEditable.observeForever {
            //check if user changed the total price
            if (isUserEditTotalPrice) {
                //the user have changed the total price, updating price per quantity accordingly
                val totalPrice =
                    (getDoubleFromTextWithSymbols(totalPriceEditable.value) ?: Double.NaN)
                val discountPercent =
                    getDoubleFromTextWithSymbols(discountPercentEditable.value) ?: Double.NaN
                val calculatedPricePerQuantity =
                    (totalPrice * 100) / (calculatedQuantity.get() * (100 - discountPercent))
                pricePerQuantityEditable.value = String.format("%f", calculatedPricePerQuantity)
            }
        }
        viewModelScope.launch {
            try {
                (loading as MutableLiveData).value = true
                //get item's available properties
                val product = productRepository.getProduct(docItem.code)
                if (product.autoQuantity) {
                    quantityCalculator = Expression(product.autoQuantityCalculationExpression)
                    (isQuantityEditable as MutableLiveData).value = false
                    calculatedQuantity.addOnPropertyChanged {
                        quantityEditable.value = "${it.get()}"
                    }
                } else {
                    (isQuantityEditable as MutableLiveData).value = true
                    quantityEditable.observeForever { calculatedQuantity.set(it.toDouble()) }
                }

                //set properties's default values foreach property that not already exist in the item
                product.properties.forEach {
                    if (!propertiesValues.any { x -> x.key == it.code })
                        setPropertyValue(it, it.defaultValue)
                }
                (properties as MutableLiveData).value = product.properties


            } catch (error: Throwable) {
                (errorMessage as MutableLiveData).value = Event(error)
            } finally {
                (loading as MutableLiveData).value = false
            }
        }
    }

    fun getPropertyValue(property: ProductProperty): Any? = propertiesValues[property.code]


    fun setTotalPriceFocusState(hasFocus: Boolean) {
        isUserEditTotalPrice = hasFocus
    }

    fun setPropertyValue(property: ProductProperty, value: Any?) {
        //TODO validate value range and type
        if (value != null)
            propertiesValues[property.code] = value
        else
            propertiesValues.remove(property.code)

        if (quantityCalculator != null) {
            //calculate the calculatedQuantity via expression using item's properties as arguments (exp: high*width)
            val args =
                propertiesValues.map { x -> Argument(x.key, "${x.value}") }.toTypedArray()
            val exp = Expression(quantityCalculator!!.expressionString, *args)
            calculatedQuantity.set(exp.calculate())
        }
    }

    fun onConfirmClicked() {
        val docItem = this.docItem.copy(
            description = descriptionEditable.value,
            details = detailsEditable.value,
            comments = commentsEditable.value,
            discountPercent = getDoubleFromTextWithSymbols(discountPercentEditable.value)
                ?: -1.0,
            quantity = calculatedQuantity.get(),
            pricePerQuantity = getDoubleFromTextWithSymbols(pricePerQuantityEditable.value)
                ?: -1.0,
            properties = propertiesValues
        )
        //Todo DocItem validation
        (confirmEvent as MutableLiveData).value = Event(docItem)
    }


    private fun calcTotalPrice(): Double? {
        val pricePerQuantity = getDoubleFromTextWithSymbols(pricePerQuantityEditable.value)
        val discountPercent = getDoubleFromTextWithSymbols(discountPercentEditable.value)
        if (discountPercent == null || pricePerQuantity == null)
            return null
        return pricePerQuantity * (100 - discountPercent) / 100 * calculatedQuantity.get()

    }

    private fun refreshTotalPrice() {
        val totalPrice = calcTotalPrice()
        if (!isUserEditTotalPrice)
            totalPriceEditable.value =
                String.format("%f", totalPrice)//removes the exp sign if exist
    }

    fun getDoubleFromTextWithSymbols(moneyText: String?): Double? {
        val double =
            moneyText?.replace(nonDoubleRegex, "")//replace all characters except digits, +-.
        return double?.toDoubleOrNull()
    }

    fun getIntFromTextWithSymbols(moneyText: String?): Int? {
        val int =
            moneyText?.replace(nonDoubleRegex, "")//replace all characters except digits, +-.
        return int?.toIntOrNull()
    }


}


class EditDocItemPropertiedViewModelFactory(
    private val productRepository: IProductRepository
) : ViewModelProvider.NewInstanceFactory() {

    lateinit var docItem: DocItem
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditDocItemPropertiedViewModel(productRepository, docItem) as T
    }
}