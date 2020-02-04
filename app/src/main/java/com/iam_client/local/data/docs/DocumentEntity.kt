package com.iam_client.local.data.docs
import androidx.room.Entity
import androidx.room.PrimaryKey

abstract class DocumentEntity
{
    var externalSN: String? = ""
    var customerSN: String = ""
    var customerName: String = ""
    var customerAddress: String = ""
    var customerFederalTaxID: String? = ""

    var date: Long? = null
    var closingDate: Long?= null
    var creationDateTime: Long?= null
    var lastUpdateDateTime: Long?= null

    var comments: String?= null
    var currency: String = ""

    var discountPercent: Double?= null
    var docTotal: Double?= null

    var isCanceled: Boolean?= null
    var isClosed: Boolean?= null


    var items: List<DocItemEntity>?= null  //save as json

    var ownerEmployeeSN: Int? = null
    var salesmanSN: Int?= null

    var totalDiscountAndRounding: Double?= null
    var vat: Double?= null
    var vatPercent: Double?= null
    var paid: Double = 0.0
    var grosProfit: Double?= null
}


@Entity(tableName = "INVOICES")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = false)
    val sn: Int) : DocumentEntity()

@Entity(tableName = "CreditNotes")
data class CreditNoteEntity(
    @PrimaryKey(autoGenerate = false)
    val sn: Int) : DocumentEntity()

@Entity(tableName = "DeliveryNotes")
data class DeliveryNoteEntity(
    @PrimaryKey(autoGenerate = false)
    val sn: Int) : DocumentEntity(){
    var supplyDate: Long? = null
}

@Entity(tableName = "Orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = false)
    val sn: Int) : DocumentEntity(){
    var supplyDate: Long? = null
}

@Entity(tableName = "Quotations")
data class QuotationEntity(
    @PrimaryKey(autoGenerate = false)
    val sn: Int) : DocumentEntity(){
    var validUntil: Long? = null
}