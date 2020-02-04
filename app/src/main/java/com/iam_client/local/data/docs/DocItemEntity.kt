package com.iam_client.local.data.docs

import java.util.*

data class DocItemEntity(

    val code: String,
    val comments: String?,
    val currency: String?,
    val description: String?,
    val details: String?,
    val discountPercent: Double?,
    val docNumber: Int?,
    val isOpen: Boolean?,
    val itemNumber: Int?,
    val pricePerQuantity: Double?,
    val quantity: Double?,
    val openQuantity: Double?,
    val properties: Map<String, Any>?,
    val visualOrder: Int?,
    val baseItemNumber: Int?,
    val baseDoc : DocReferenceEntity?,
    val followDoc : DocReferenceEntity?
){
    enum  class DocType { Invoice, Order, Quotation, CreditNote, DeliveryNote, DepositNote, Recipt, Other }
    data class DocReferenceEntity(
        val docNumber : Long,
        val docType : DocType
    )
}