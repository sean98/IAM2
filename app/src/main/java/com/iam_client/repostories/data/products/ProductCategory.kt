package com.iam_client.repostories.data.products

import java.io.Serializable

data class ProductCategory (
    val code:Int,
    val name :String,
    val pictureURL :String?
) : Serializable