package com.iam_client.remote.data
import androidx.annotation.Keep
import  java.io.Serializable

@Keep
data class CustomerDTO (
    val cid : String?,
    val name : String?,
    val federalTaxID : String?,
    val type : Types?,
    val isActive : Boolean?,

    val groupSN : Int?,
    val billingAddress  :  AddressDTO?,
    val shippingAddress  :  AddressDTO?,
    val phone1 : String?,
    val phone2 : String?,
    val cellular : String?,
    val email : String?,
    val fax : String?,
    val comments : String?,
    val salesmanCode : Int?,
    val balance :Double,
    val currency : String?,
    val creationDateTime : Long?,
    val lastUpdateDateTime : Long?
) :Serializable{

    enum class Types{
        Private,
        Company,
        Employee
    }
}

data class CardGroupDTO(val sn : Int,val name:String)