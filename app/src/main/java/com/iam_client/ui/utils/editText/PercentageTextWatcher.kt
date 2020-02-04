package com.iam_client.ui.utils.editText

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat

open class SymbolTextWatcher(private val editText: EditText, private val symbol: String) : TextWatcher {
    var maxValue : Double = Double.POSITIVE_INFINITY
    var minValue : Double = Double.NEGATIVE_INFINITY
    var textOnEmpty =""

    private var current: String = ""
    private val formatterFactional = DecimalFormat("###,###,###,###.##")
    private val formatterInteger = DecimalFormat("###,###,###,###")
    private var hasFractionalPart = false
    private val nonDoubleChars = Regex("[^-+0-9.]")
    private var nonDoubleCharDeletedIndex :Int? = null
    private val isNonDoubleCharDeleted : Boolean
        get() = nonDoubleCharDeletedIndex!=null
    init {
        formatterFactional.isDecimalSeparatorAlwaysShown = true
    }
    override fun afterTextChanged(s: Editable?) {
        if (!s.isNullOrEmpty()) {
            editText.removeTextChangedListener(this)
            var selectionStart = editText.selectionStart

            var text = s.toString()
            if (text != current) {
                if(isNonDoubleCharDeleted){
                    //delete the digit before
                    val firstDoubleCharIndexBefore = text.slice(0 until nonDoubleCharDeletedIndex!!)
                        .indexOfLast {!it.toString().matches(nonDoubleChars)}
                    var firstDoubleCharIndexAfter = nonDoubleCharDeletedIndex!!+text.slice(nonDoubleCharDeletedIndex!! until  text.length)
                        .indexOfFirst {!it.toString().matches(nonDoubleChars)}
                    if(firstDoubleCharIndexAfter== -1)
                        firstDoubleCharIndexAfter = text.length
                    text = text.removeRange(firstDoubleCharIndexBefore , firstDoubleCharIndexAfter+1)
                    //set selection position
                    selectionStart = firstDoubleCharIndexBefore-1
                }

                val initLen = text.length
                var cleanText = text.replace(nonDoubleChars, "")
                if(cleanText == ".")
                    cleanText = "0."
                var doubleFromText = cleanText.toDoubleOrNull()
                if(doubleFromText!=null){
                    if(doubleFromText > maxValue)
                        doubleFromText = maxValue
                    else if(doubleFromText < minValue)
                        doubleFromText = minValue
                }

                val formattedText: String = when {
                    doubleFromText == null -> {textOnEmpty}
                    !editText.isFocused && doubleFromText == doubleFromText.toInt().toDouble()->"${formatterInteger.format(doubleFromText)}$symbol"
                    editText.isFocused && cleanText.endsWith(".0")->"${formatterInteger.format(doubleFromText)}.0$symbol"
                    hasFractionalPart  -> "${formatterFactional.format(doubleFromText)}$symbol"
                    else -> "${formatterInteger.format(doubleFromText)}$symbol"
                }

                editText.setText(formattedText)
                val endLen = formattedText.length
                val selection = selectionStart + (endLen - initLen)

                if(selection in (endLen - symbol.length)..(endLen+1) && endLen - symbol.length>=0)
                    editText.setSelection(endLen - symbol.length)
                else if (selection in 1..endLen)
                    editText.setSelection(selection)

                current = formattedText
            }
            editText.addTextChangedListener(this)
        }
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        if(!s.isNullOrBlank() &&s.length>start && s[start].toString().matches(nonDoubleChars) && count==1 && after ==0)
            nonDoubleCharDeletedIndex = start
        else
            nonDoubleCharDeletedIndex = null


    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        hasFractionalPart = s.toString().contains(formatterFactional.decimalFormatSymbols.decimalSeparator)

    }


}

class PercentageTextWatcher( editText: EditText,maxValue:Double? = null)
    : SymbolTextWatcher(editText,"%") {
   init {
       this.maxValue = maxValue?:100.0
       minValue = 0.0
       textOnEmpty = "0%"
   }
}

class CurrencyTextWatcher( editText: EditText,  currency: String)
    : SymbolTextWatcher(editText,currency)

