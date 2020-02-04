package com.iam_client.local.data.products

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PRODUCT_CATEGORIES")
data class ProductCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val code:Int,
    val name :String,
    val pictureURL :String?,
    val lastUpdateDate: Long?,
    val creationDate: Long?
)