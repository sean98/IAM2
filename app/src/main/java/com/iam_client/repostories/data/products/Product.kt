package com.iam_client.repostories.data.products


data class Product(
    val code: String,
    val groupCode: Int,
    val name: String,
    val nameForegin: String,
    val barcode: String?,
    val pictureURL: String?,
    val isActive: Boolean,
    val isForSell: Boolean,
    val isForBuy: Boolean,
    val properties: List< ProductProperty>,
    val autoQuantityCalculationExpression :String?
)
{
    val autoQuantity:Boolean
        get()=!autoQuantityCalculationExpression.isNullOrBlank()
}