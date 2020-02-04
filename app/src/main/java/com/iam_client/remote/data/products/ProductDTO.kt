package com.iam_client.remote.data.products

import com.iam_client.repostories.data.products.ProductProperty

data class ProductDTO(
    val code : String,
    val groupCode : Int,
    val name : String?,
    val nameForegin : String?,
    val barcode :String?,
    val pictureURL :String?,
    val isActive : Boolean,
    val isForSell :Boolean,
    val isForBuy :Boolean,
    val properties : List<ProductPropertyDTO>?,
    val creationDateTime: Long?,
    val lastUpdateDateTime:Long?,
    val autoQuantityCalculationExpression :String?
)