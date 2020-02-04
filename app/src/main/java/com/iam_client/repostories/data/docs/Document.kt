package com.iam_client.repostories.data.docs

import java.util.Date


abstract class Document
{
    open var sn: Int? = null
    var externalSN: String? = ""
    var customerSN: String = ""
    var customerName: String = ""
    var customerAddress: String = ""
    var customerFederalTaxID: String? = ""

    var date: Date? = null
    var closingDate: Date?= null
    var creationDate: Date?= null

    var comments: String?= null
    var currency: String = ""

    var discountPercent: Double?= null
    var docTotal: Double?= null

    var isCanceled: Boolean= false
    var isClosed: Boolean= false

    var items: List<DocItem>?= null

    var ownerEmployeeSN: Int? = null
    var salesmanSN: Int?= null

    var totalDiscountAndRounding: Double = 0.0
    var vatPercent: Double?= null
    var paid: Double = 0.0
    var grossProfit: Double?= null

    var vat: Double?= null

    val totalBeforeVatAndDiscount :Double
        get() = (docTotal?: 0.0) - (vat?:0.0) + (totalDiscountAndRounding)

    val needToPay :Double
        get() = (docTotal?: 0.0) -paid

    abstract val type : Type

    enum class Type{Invoice,CreditNote,Order,DeliveryNote,Quotation}
}


data class Invoice(override var sn: Int?) : Document(){
    override val type = Type.Invoice
}

data class CreditNote(override var sn: Int?) : Document(){
    override val type = Type.CreditNote
}

data class DeliveryNote(override var sn: Int?) : Document(){
    override val type = Type.DeliveryNote
    var supplyDate: Date? = null
}

data class Order(override var sn: Int?) : Document(){
    override val type = Type.Order
    var supplyDate: Date? = null
}

data class Quotation(override var sn: Int?) : Document(){
    override val type = Type.Quotation
    var validUntil: Date? = null
}