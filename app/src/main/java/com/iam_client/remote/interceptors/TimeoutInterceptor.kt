package com.iam_client.remote.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
const val WRITE_TIMEOUT = "WRITE_TIMEOUT"
const val READ_TIMEOUT = "READ_TIMEOUT"
const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"

class TimeoutInterceptor : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val connectNew = request.header(CONNECT_TIMEOUT)
        val readNew = request.header(READ_TIMEOUT)
        val writeNew = request.header(WRITE_TIMEOUT)

        var connectTimeout = chain.connectTimeoutMillis()
        var readTimeout = chain.readTimeoutMillis()
        var writeTimeout = chain.writeTimeoutMillis()

        if (!connectNew.isNullOrEmpty()){
            Log.d("TimeoutInterceptor","CONNECT_TIMEOUT intercepted, changed to $connectNew")
            connectTimeout = Integer.valueOf(connectNew)
        }

        if (!readNew.isNullOrEmpty()){
            Log.d("TimeoutInterceptor","READ_TIMEOUT intercepted, changed to $readNew")
            readTimeout = Integer.valueOf(readNew)

        }
        if (!writeNew.isNullOrEmpty()){
            Log.d("TimeoutInterceptor","WRITE_TIMEOUT intercepted, changed to $writeNew")
            writeTimeout = Integer.valueOf(writeNew)
        }

        val builder = request.newBuilder()
            .removeHeader(CONNECT_TIMEOUT)
            .removeHeader(READ_TIMEOUT)
            .removeHeader(WRITE_TIMEOUT)
        return chain
            .withConnectTimeout(connectTimeout,TimeUnit.MILLISECONDS)
            .withReadTimeout(readTimeout,TimeUnit.MILLISECONDS)
            .withWriteTimeout(writeTimeout,TimeUnit.MILLISECONDS)
            .proceed(builder.build())
    }
}