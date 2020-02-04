package com.iam_client.remote.interceptors

import android.content.Context
import android.util.Log
import com.iam_client.remote.apiServices.utils.IAuthorizationWebService
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

const val PASSWORD_KEY = "password"
const val USER_NAME_KEY = "username"
const val TOKEN_KEY = "token"
const val NO_AUTHORIZATION_HEADER = "No-Authorization"
const val AUTHORIZATION_HEADER = "Authorization"
const val TIME_OFFSET = 10 * 60 * 1000 //10 minutes (by millis)


class TokenInterceptor(context: Context) : Interceptor, KodeinAware {
    //TODO create login service
    private val appContext = context.applicationContext
    override val kodein by closestKodein(appContext)

    private val authorizationService: IAuthorizationWebService by instance()

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        var request = chain.request()

        //Check if the request need authentication
        if (request.header(NO_AUTHORIZATION_HEADER) == null) {
            try {
                val authHeader = authorizationService.getAuthorizationHeader()
                request = request.newBuilder()
                    .addHeader(AUTHORIZATION_HEADER, authHeader)
                    .build()

            } catch (t: Throwable) {
                //if an error accrued return an error http response
                Log.e("Token re-login", "token regain error:${t.message}")
                return@runBlocking Response.Builder()
                    .code(401) //Whatever error code
                    .protocol(Protocol.HTTP_2)
                    .message("Can't re-login , Client error!")
                    .body(ResponseBody.create(MediaType.get("text/html; charset=utf-8"), ""))
                    .request(chain.request())
                    .build()
            }


        }
        return@runBlocking chain.proceed(request)
    }


}
