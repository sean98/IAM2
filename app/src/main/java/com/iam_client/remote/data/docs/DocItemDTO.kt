package com.iam_client.remote.data.docs


data class DocItemDTO(
    val code: String,
    val comments: String?,
    val currency: String?,
    val description: String?,
    val details: String?,
    val discountPercent: Double?,
    val docNumber: Int?,
    val isOpen: Boolean?,
    val itemNumber: Int?,
    val visualOrder: Int?,
    val pricePerQuantity: Double?,
    val quantity: Double?,
    val openQuantity: Double?,
    val properties: Map<String,Any>?,
    val baseItemNumber: Int?,
    val baseDoc : DocReferenceDTO?,
    val followDoc : DocReferenceDTO?
    ){
    enum  class DocType { Invoice, Order, Quotation, CreditNote, DeliveryNote, DepositNote, Receipt, Other }

    data class DocReferenceDTO(
        val docNumber : Long,
        val docType : DocType
    )
}