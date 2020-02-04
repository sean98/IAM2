package com.iam_client.remote.data

import  java.io.Serializable

data class AddressDTO (var id : String? ,
                       var country : String? ,
                       var city : String? ,
                       var block : String?,
                       var street : String? ,
                       var numAtStreet : String? ,
                       var apartment : String?,
                       var zipCode : String?
) :Serializable