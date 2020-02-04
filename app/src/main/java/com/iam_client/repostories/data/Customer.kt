package com.iam_client.repostories.data

import android.content.res.Resources
import com.iam_client.R
import java.io.Serializable

data class Customer (
    val cid : String?,
    val balance :Double,
    val currency : String,
    var name : String,
    var federalTaxID : String,
    var group : CardGroup,
    var type : Type,
    var isActive :Boolean,
    var billingAddress : Address?,
    var shippingAddress : Address?,
    var phone1 : String,
    var phone2 : String,
    var cellular : String,
    var email : String,
    var fax : String,
//more information
    var comments : String,
    var salesman : Salesman
) : Serializable
{

    enum class Type{
        Private,
        Company,
        Employee;

    }

    companion object{
        fun getEmpty()=Customer(
            cid = null,
            balance = 0.0,
            currency = "",
            name = "",
            federalTaxID = "",
            group = CardGroup(Int.MIN_VALUE,"Empty"),//TODO
            type = Type.Company,
            isActive = true,
            billingAddress = null,
            shippingAddress = null,
            phone1 = "",
            phone2 = "",
            cellular = "",
            email = "",
            fax = "",
            comments = "",
            salesman = Salesman(Int.MIN_VALUE,"Empty",null,null, false)//TODO
        )
    }
}

data class CardGroup(val sn : Int,val name:String): Serializable

