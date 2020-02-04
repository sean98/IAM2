package com.iam_client


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import com.android.volley.toolbox.RequestFuture
import org.json.JSONObject
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * Instrumented test, which will handleResult on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("com.iam_client", appContext.packageName)
    }





    private fun trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
            val context = SSLContext.getInstance("TLS")
            context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                   return  arrayOf()
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>, authType: String)
                {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>, authType: String) {
                }
            }), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.socketFactory
            )
        } catch (e: Exception) { // should never happen
            e.printStackTrace()
        }

    }
}
