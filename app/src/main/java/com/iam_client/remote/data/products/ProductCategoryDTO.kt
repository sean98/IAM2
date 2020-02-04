package com.iam_client.remote.data.products

data class ProductCategoryDTO (
    val code:Int,
    val name :String?,
    val pictureURL :String?,
    val lastUpdateDate: Long?,
    val creationDate: Long?
)