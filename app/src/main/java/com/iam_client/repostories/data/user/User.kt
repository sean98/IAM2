package com.iam_client.repostories.data.user

data class User(
    val userId: Int,
    val role: Role
) {
    var employeeSN:Int? = null

    enum class Role { Admin, Standard }
}


