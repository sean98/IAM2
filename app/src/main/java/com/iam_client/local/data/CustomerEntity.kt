package com.iam_client.local.data

import androidx.room.*
import com.iam_client.repostories.data.Customer

@Entity(tableName = "CUSTOMERS")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = false)
    val cid : String,
    var name : String,
    var federalTaxID : String,
    var type : Types,
    var isActive : Boolean,
    var groupSN : Int,
    @Embedded(prefix = "BILLING_ADDRESS_")
    var billingAddress  :  AddressEntity?,
    @Embedded(prefix = "SHIPPING_ADDRESS_")
    var shippingAddress  :  AddressEntity?,
    var phone1 : String?,
    var phone2 : String?,
    var cellular : String?,
    var email : String?,
    var fax : String?,
    var comments : String?,
    var salesmanCode : Int,
    var balance :Double,
    var currency : String,
    var creationDateTime : Long,
    var lastUpdateDateTime : Long?
)
{
    enum class Types {Private, Company, Employee }
}
@Entity(tableName = "CARDGROUPS")
data class CardGroupEntity( @PrimaryKey(autoGenerate = false) val sn : Int,val name:String)

@Fts4(contentEntity = CustomerEntity::class)
@Entity(tableName = "CustomerFTS")
data class CustomerFTS(
    val cid : String,
    val name : String,
    val federalTaxID : String,
    val phone1 : String?,
    val phone2 : String?,
    val cellular : String?,
    val email : String?,
    val fax : String?,
    @ColumnInfo(name  = "BILLING_ADDRESS_city")
    var billingAddressCity  :  String?,
    @ColumnInfo(name = "SHIPPING_ADDRESS_city")
    var shippingAddressCity  :  String?
)
