package com.iam_client.remote.data.products

data class ProductPropertyDTO(
    val code:String,
    val defaultValue: Any?,
    val type: PropertyType,
    val minValue: Float?,
    val maxValue: Float?,
    val choices: List<String>?,
    val fromDate: Long?,
    val toDate: Long?,
    val maxDaysFromNow: Int?,
    val uom: String?
) {
    enum class PropertyType { Decimal, Int, Text, Choice, Date }
}