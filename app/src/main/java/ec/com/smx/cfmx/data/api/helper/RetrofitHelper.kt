package ec.com.smx.cfmx.data.api.helper

import android.content.Context
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class RetrofitHelper(val cookieManager: CookieManager, context: Context) {

    companion object {
        const val CLIENT_CONNECTION_TIMEOUT = 20000
        const val CLIENT_READ_TIMEOUT = 100000
        const val OPERATING_SYSTEM_KEY = "OS"
        const val OPERATING_SYSTEM_VALUE = "Android"
        const val APP_KEY = "App-Master"
        const val APP_VALUE = "Master"
    }

    /**
     * Get Retrofit object building by OkHttpClient
     */
    fun getRetrofitBase(url: String, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
    }

    /**
     * Get OkHttpClient with no header personalization
     */
    fun getOkHttpClient(): OkHttpClient {
        return this.getOkHttpClientBase()
    }


    /**
     * Get OkHttpClient with header personalization
     */
    fun getOkHttpClientHeaderSession(token: String): OkHttpClient {
        val httpClient = this.getOkHttpClientBase()
        httpClient.interceptors().add(Interceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
            requestBuilder.addHeader("Cookie", "JSESSIONID=$token")
            requestBuilder.addHeader(OPERATING_SYSTEM_KEY, OPERATING_SYSTEM_VALUE)
            requestBuilder.addHeader(APP_KEY, APP_VALUE)
            requestBuilder.method(original.method(), original.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        })
        return httpClient
    }

    /**
     * Get OkHttpClient with time personalization
     */
    private fun getOkHttpClientBase(): OkHttpClient {
        val httpClient = OkHttpClient()
        httpClient.setConnectTimeout(CLIENT_CONNECTION_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
        httpClient.setReadTimeout(CLIENT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        httpClient.cookieHandler = this.cookieManager
        return httpClient
    }
}
