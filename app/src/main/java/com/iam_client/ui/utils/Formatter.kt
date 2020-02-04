package com.iam_client.ui.utils

import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.Date

object Formatter {
    private val dateF = DateFormat.getDateInstance()
    var currency : String ="$"
    private val doubleMoneyF = DecimalFormat("###,###.##").apply {
        //maximumFractionDigits = 2
        roundingMode = RoundingMode.FLOOR
    }
    private val doubleQuantityF = DecimalFormat("###,###.###")

    fun Date.formatString():String = dateF.format(this)

    fun Double.formatMoneyString(currencySymbol:String? = null) :String{
        return "${doubleMoneyF.format(this)}${currencySymbol?:currency}"
    }

    fun Double.formatQuantityString(quantitySymbol:String? = null) :String{
        return "${doubleQuantityF.format(this)}${quantitySymbol?:""}"
    }
}