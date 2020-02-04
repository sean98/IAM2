package com.iam_client.remote.data

data class EmployeeDTO (
    val sn : Int?,
    //basic information
    val firstName : String?,
    val middleName : String?,
    val lastName : String? ,
    val id : String?,
    val gender : Genders,
    val birthday : Long?,

    val isActive : Boolean,

    //contact information
    val homePhone : String?,
    val officePhone : String?,
    val workCellular : String?,
    val fax : String? ,

    //job information
    val department : DepartmentDTO? ,
    val position : JobPositionDTO?,
    val managerSN : Int? ,
    val salesmanSN : Int?,

    //address information
    val homeAddress  :  AddressDTO? ,
    val workAddress  :  AddressDTO? ,

    //more information
    val picPath : String?
)
{
    enum class Genders { Male, Female}

    data class JobPositionDTO(val code : Int? ,val name : String? ,val description : String?)

    data class DepartmentDTO(val code : Int? ,val name : String? ,val description : String?)

}