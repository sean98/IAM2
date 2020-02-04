package com.iam_client.remote.data

data class CompanyDTO(var name : String = "") {
    var phone : String? = null
    var fax : String? = null
    var email : String? = null
    var webSite : String? = null
    var defaultCurrency : String? = null
    var address  :  AddressDTO? = null
}