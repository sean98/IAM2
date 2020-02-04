package com.iam_client.repostories.data.docs

data class DocItem (
    val code: String,
    val docNumber: Int?,
    var itemNumber: Int?,
    var description: String?,
    var details: String?,
    var comments: String?,
    var isOpen: Boolean,
    var quantity: Double,
    val openQuantity: Double?,
    var pricePerQuantity: Double,
    var currency: String?,
    var discountPercent: Double,
    val properties: MutableMap<String, Any>,
    val visualOrder: Int?,
    val baseItemNumber: Int?,
    val baseDoc : DocReference?,
    val followDoc : DocReference?
){
    val totalPrice : Double
        get() =(quantity) * (pricePerQuantity)  * (100-(discountPercent))/100


    enum  class DocType { Invoice, Order, Quotation, CreditNote, DeliveryNote, DepositNote, Recipt, Other }
    data class DocReference(
        val docNumber : Long,
        val docType : DocType
    )
}