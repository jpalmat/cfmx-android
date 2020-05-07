package ec.com.smx.cfmx.data.api

import android.content.Context
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.api.helper.RetrofitHelper
import retrofit.Retrofit
import java.net.CookieManager

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
open class ServiceCall(protected var context: Context) {

    val url: String = this.context.getString(R.string.base_url)

    protected fun getClientWithHeader(url: String, token: String): Retrofit {
        // Build a helper
        val retrofitHelper = RetrofitHelper(CookieManager(), this.context)
        // Build a client
        val httpClient = retrofitHelper.getOkHttpClientHeaderSession(token)
        // Return client with header
        return retrofitHelper.getRetrofitBase(url, httpClient)
    }
}
