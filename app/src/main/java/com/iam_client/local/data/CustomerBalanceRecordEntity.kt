package com.iam_client.local.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CustomerBalanceRecords")
data class CustomerBalanceRecordEntity (
    @PrimaryKey(autoGenerate = false)
    val sn:Int,
    val balanceDebt: Double,
    val date: Long,
    val debt: Double,
    val doc1SN: String?,
    val doc2SN: String?,
    val memo: String?,
    val ownerSN: String?,
    val type: Type,
    val currency: String
){
    enum class Type{
        Invoice,
        Receipt,
        CreditInvoice,
        Journal,
        Other
    }
}