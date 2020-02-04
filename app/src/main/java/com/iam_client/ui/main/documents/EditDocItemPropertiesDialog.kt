package com.iam_client.ui.main.documents


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders

import com.iam_client.R
import com.iam_client.databinding.FragmentEditDocItemDialogBinding
import com.iam_client.repostories.data.docs.DocItem
import com.iam_client.repostories.data.products.ProductProperty
import com.iam_client.ui.utils.editText.CurrencyTextWatcher
import com.iam_client.ui.utils.editText.PercentageTextWatcher
import com.iam_client.utills.reactive.observe
import com.iam_client.utills.reactive.observeEvent
import com.iam_client.viewModels.main.documents.EditDocItemPropertiedViewModel
import com.iam_client.viewModels.main.documents.EditDocItemPropertiedViewModelFactory
import kotlinx.android.synthetic.main.fragment_edit_doc_item_dialog.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import android.widget.LinearLayout
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.android.synthetic.main.item_porperty_text_input.view.*
import java.io.IOException
import java.util.*


class EditDocItemPropertiesDialog(docItem: DocItem) : DialogFragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: FragmentEditDocItemDialogBinding
    private val viewModel by lazy {
        val modelFactory: EditDocItemPropertiedViewModelFactory by instance()
        modelFactory.docItem = docItem
        ViewModelProviders
            .of(this, modelFactory)
            .get(EditDocItemPropertiedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_doc_item_dialog, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel


        //create dynamic properties's views
        viewModel.properties.observe(this)  {
            it.forEach { property ->
                val propertyField = createPropertyView(property)
                propertyField.tag = property
                fields_list.addView(propertyField,propertyField.layoutParams)
            }
        }

        viewModel.isQuantityEditable.observe(this){
            quantityTextField.isEnabled = it
            quantityTextField.isFocusable = it
        }

        viewModel.confirmEvent.observeEvent(this){
            onConfirm(it)
            this@EditDocItemPropertiesDialog.dismiss()
        }

        priceTextField.onFocusChange { _, hasFocus ->  viewModel.setTotalPriceFocusState(hasFocus)}

        discountPercentTextField.addTextChangedListener(PercentageTextWatcher(discountPercentTextField))

        priceTextField.addTextChangedListener(CurrencyTextWatcher(priceTextField,viewModel.currency?:""))
        pricePerQuantityTextField.addTextChangedListener(CurrencyTextWatcher(pricePerQuantityTextField,viewModel.currency?:""))
    }





    private fun createPropertyView (productProperty : ProductProperty) : View{
        //create appropriate view foreach data type and bind the value to the view model
       val view = inflate(context,R.layout.item_porperty_text_input,null)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val textLayout = view.textLayout
        textLayout.hint =viewModel.propertiesLocalName.value?.get(productProperty)?:productProperty.code
        viewModel.propertiesLocalName.observe(this){
            if(it[productProperty]!=null)
                textLayout.hint = it[productProperty]
        }
        val field = view.textField
        when(productProperty.type){
            ProductProperty.PropertyType.Text->field.inputType = InputType.TYPE_CLASS_TEXT
            ProductProperty.PropertyType.Decimal->field.inputType =  InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            ProductProperty.PropertyType.Int->field.inputType = InputType.TYPE_CLASS_NUMBER
            else->TODO("implement type")

        }
        field.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //bind field's value to the ViewModel
                viewModel.setPropertyValue(productProperty,getPropertyValueFromText(productProperty,p0.toString()))
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

//        if(!productProperty.uom.isNullOrEmpty())
//            field.addTextChangedListener(SymbolTextWatcher(field,productProperty.uom))
        val propertyValue = viewModel.getPropertyValue(productProperty)

        field.setText("$propertyValue")
        return view
    }

    private fun getPropertyValueFromText(productProperty: ProductProperty,value:String):Any?{
        if(value.isBlank())
            return null
        return when(productProperty.type){
            ProductProperty.PropertyType.Text-> value
            ProductProperty.PropertyType.Decimal-> viewModel.getDoubleFromTextWithSymbols(value)
            ProductProperty.PropertyType.Int->viewModel.getIntFromTextWithSymbols(value)
            else->TODO("implement type")
        }
    }

    var onConfirm:(DocItem)->Unit = {}

}
