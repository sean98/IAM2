package com.iam_client.repostories.user

import androidx.lifecycle.LiveData
import com.iam_client.repostories.data.user.User

interface IUserRepository {

     fun getLoggedInUserLive(): LiveData<User?>
    suspend fun getLoggedInUser(): User?

    suspend fun login(username: String, password: String): User

    suspend fun logout()


}