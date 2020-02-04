package com.iam_client.local.data

import androidx.room.Entity

data class AddressEntity(
    val id: String,
    var country : String?,
    var city : String?,
    var block : String?,
    var street : String?,
    var numAtStreet : String?,
    var apartment : String?,
    var zipCode : String?
)