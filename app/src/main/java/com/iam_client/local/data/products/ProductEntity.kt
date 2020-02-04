package com.iam_client.local.data.products

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iam_client.repostories.data.products.ProductProperty

@Entity(tableName = "PRODUCTS")
data class ProductEntity (
    @PrimaryKey(autoGenerate = false)
    val code: String,
    val groupCode: Int,
    val name: String,
    val nameForegin: String,
    val barcode: String?,
    val pictureURL: String?,
    val isActive: Boolean,
    val isForSell: Boolean,
    val isForBuy: Boolean,
    val properties: List<ProductPropertyEntity>,
    val creationDateTime: Long?,
    val lastUpdateDateTime:Long?,
    val autoQuantityCalculationExpression :String?
)