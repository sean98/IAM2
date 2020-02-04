package com.iam_client.repostories.data

import java.io.Serializable

data class Salesman (
    val sn : Int,
    val name : String,
    val mobile : String? ,
    val email : String?,
    val isActive : Boolean
): Serializable