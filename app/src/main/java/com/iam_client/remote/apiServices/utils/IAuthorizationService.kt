package com.iam_client.remote.apiServices.utils

interface IAuthorizationService {
    suspend fun login(username: String, password: String): UserDTO

    suspend fun getLoggedInUser():UserDTO?

    suspend fun logout()





}

interface IAuthorizationWebService {
    suspend fun getAuthorizationHeader(): String
}


