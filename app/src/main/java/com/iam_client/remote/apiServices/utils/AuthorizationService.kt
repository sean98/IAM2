package com.iam_client.remote.apiServices.utils

import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.iam_client.local.secure.ISecureStoring
import com.iam_client.remote.apiServices.LoginApiService
import kotlinx.serialization.enumFromName
import java.lang.RuntimeException
import java.util.*

const val TOKEN_KEY = "TOKEN_KEY"
const val PASSWORD_KEY = "password"
const val USER_NAME_KEY = "username"
const val TIME_OFFSET = 10 * 60 * 1000 //10 minutes (by millis)

class AuthorizationService(
    private val loginApiService: LoginApiService,
    private val secureStoring: ISecureStoring
) : IAuthorizationService, IAuthorizationWebService {
    private var cachedToken: String? = null


    override suspend fun login(username: String, password: String): UserDTO {
        //ask the api for an authorization token
        val token = loginApiService.login(username, password)
        if (!verifyToken(token))
            throw RuntimeException("Authorization token verification error")

        //save the authorization token in a secure file
        secureStoring.write(TOKEN_KEY, token.toByteArray())
        secureStoring.write(USER_NAME_KEY, username.toByteArray())
        secureStoring.write(PASSWORD_KEY, password.toByteArray())
        cachedToken = token
        return UserDTO(token)
    }

    override suspend fun logout() {
        //TODO clear user information
        secureStoring.write(TOKEN_KEY, "".toByteArray())
        secureStoring.write(USER_NAME_KEY, "".toByteArray())
        secureStoring.write(PASSWORD_KEY, "".toByteArray())
        cachedToken = null
    }

    override suspend fun getLoggedInUser(): UserDTO? {
        if (cachedToken == null) {
            val tokenByteArray = secureStoring.read(TOKEN_KEY)
            cachedToken = if (tokenByteArray != null) String(tokenByteArray) else null
        }
        val token = cachedToken
        if (token != null && verifyToken(token))
            return UserDTO(token)
        return null
    }


    private fun verifyToken(token: String): Boolean {
        val jwt = JWT.decode(token)
        val isExpired = jwt.expiresAt < Date(System.currentTimeMillis() + TIME_OFFSET)
        if(isExpired)
            Log.e("Token Validation", "token is expired :${jwt.expiresAt}")

        return !isExpired
    }

    override suspend fun getAuthorizationHeader(): String {
        //if token is not cached on RAM try to get from secure hard file
        if (cachedToken == null) {
            val tokenByteArray = secureStoring.read(TOKEN_KEY)
            cachedToken = if (tokenByteArray != null) String(tokenByteArray) else null
        }

        //if the token is cached - check if expired or invalid
        if (cachedToken != null && !verifyToken(cachedToken?:"")){
            //refresh and ask for another token
            try {
                cachedToken = null
                Log.e("Token refresh", "Must replace with TOKEN refresh call")
                //TODO refresh token! don't persist user's information
                val userName = String(secureStoring.read(USER_NAME_KEY)!!)
                val password = String(secureStoring.read(PASSWORD_KEY)!!)
                login(userName, password)///TODO must replace it with refresh token call
            } catch (t: Throwable) {
                Log.e("Token refresh", "refreshing token error:${t.message}")
                throw t
            }
        }
        return "Bearer $cachedToken"
    }

    //newInstance singleton instance
    companion object {
        @Volatile
        private var instance: AuthorizationService? = null

        fun getInstance(
            loginApiService: LoginApiService,
            secureStoring: ISecureStoring
        ) =
            instance ?: synchronized(this) {
                instance ?: AuthorizationService(loginApiService, secureStoring)
                    .also { instance = it }
            }
    }
}


class UserDTO(token: String) {
    val jwt: DecodedJWT = JWT.decode(token)!!

    inline fun <reified T> getTokenClaim(claimKey: String): T? {
        if (!jwt.claims.containsKey(claimKey) || jwt.claims[claimKey]!!.isNull)
            return null
        val claim = jwt.claims[claimKey]!!
        return when (T::class) {
            String::class -> claim.asString() as T?
            Int::class -> claim.asInt() as T?
            Long::class -> claim.asLong() as T?
            Double::class -> claim.asDouble() as T?
            Boolean::class -> claim.asBoolean() as T?
            Date::class -> claim.asDate() as T?
            else -> throw RuntimeException("Unsupported claim type")
        }
    }

    inline fun <reified T : Enum<T>> getTokenClaimEnum(claimKey: String): T? {
        if (!jwt.claims.containsKey(claimKey) || jwt.claims[claimKey]!!.isNull)
            return null
        val claim = jwt.claims[claimKey]!!
        return try {
            enumFromName(T::class, claim.asString())
        } catch (error: Throwable) {
            null
        }

    }
}



