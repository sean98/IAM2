package com.iam_client.remote.data.docs


abstract class DocumentDTO
{
    open var sn: Int? = null
    var externalSN: String? = ""
    var customerSN: String? = ""
    var customerName: String? = ""
    var customerAddress: String? = ""
    var customerFederalTaxID: String? = ""

    var date: Long? = null
    var closingDateTime: Long?= null
    var creationDateTime: Long?= null
    var lastUpdateDateTime: Long?= null

    var comments: String?= null
    var currency: String = ""

    var discountPercent: Double?= null
    var docTotal: Double?= null

    var isCanceled: Boolean?= null
    var isClosed: Boolean?= null

    var items: List<DocItemDTO>?= null

    var ownerEmployeeSN: Int? = null
    var salesmanSN: Int?= null

    var totalDiscountAndRounding: Double?= null
    var vat: Double?= null
    var vatPercent: Double?= null
    var paid: Double?= null
    var grosProfit: Double?= null
}


 class InvoiceDTO() : DocumentDTO()

 class CreditNoteDTO() : DocumentDTO()

 class DeliveryNoteDTO() : DocumentDTO(){
    var supplyDate: Long? = null
}

 class OrderDTO() : DocumentDTO(){
    var supplyDate: Long? = null
}
 class QuotationDTO() : DocumentDTO(){
    var validUntil: Long? = null
}