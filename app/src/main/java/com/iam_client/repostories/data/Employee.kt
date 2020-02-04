package com.iam_client.repostories.data

data class Employee(val sn : Int?,
    //basic information
                    val firstName : String?,
                    val middleName : String?,
                    val lastName : String?,
                    val id : String?,
                    val gender : Genders,
                    val birthday : Long?,

                    val isActive : Boolean = true,

    //contact information
                    var homePhone : String?,
                    var officePhone : String?,
                    var workCellular : String?,
                    var fax : String?,

    //job information
                    val department : Department?,
                    val position : JobPosition?,
                    val managerSN : Int?,
                    val salesmanSN : Int?,

    //address information
                    val homeAddress  :  Address?,
                    val workAddress  :  Address?,

    //more information
                    val picUrl : String?
)
{
    enum class Genders { Male, Female}

    data class JobPosition(val code : Int? ,var name : String? ,var description : String?)

    data class Department(val code : Int? ,var name : String? ,var description : String?)

}