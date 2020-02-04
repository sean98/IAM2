package com.iam_client.local.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.iam_client.remote.data.AddressDTO
import java.sql.Date

@Entity(tableName = "EMPLOYEES")
data class EmployeeEntity (
    @PrimaryKey(autoGenerate = false)
    val sn : Int,
    //basic information
    val firstName : String?,
    val middleName : String?,
    val lastName : String?,
    val id : String?,
    val gender : Genders,
    val birthday : Long?,

    val isActive : Boolean,

    //contact information
    val homePhone : String?,
    val officePhone : String?,
    val workCellular : String?,
    val fax : String?,

    //job information
    @Embedded(prefix = "department_")
    val department : DepartmentEntity?,
    @Embedded(prefix = "position_")
    val position : JobPositionEntity?,
    val managerSN : Int?,
    val salesmanSN : Int?,

    //address information
    @Embedded(prefix = "homeAddress_")
    val homeAddress  :  AddressEntity?,
    @Embedded(prefix = "workAddress_")
    val workAddress  :  AddressEntity?,

    //more information
    val picUrl : String?
)
{
    enum class Genders { Male, Female}

    data class JobPositionEntity(val code : Int, val name : String?, val description : String?)

    data class DepartmentEntity(val code : Int, val name : String?, val description : String?)


}



