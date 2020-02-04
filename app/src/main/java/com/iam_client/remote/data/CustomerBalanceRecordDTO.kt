package com.iam_client.remote.data

data class CustomerBalanceRecordDTO(
    val sn: Int,
    val balanceDebt: Double,
    val date: Long,
    val debt: Double,
    val doc1SN: String?,
    val doc2SN: String?,
    val memo: String?,
    val ownerSN: String?,
    val type: Type,
    val currency: String?
) {
    enum class Type {
        Invoice,
        Receipt,
        CreditInvoice,
        Journal,
        Other
    }
}