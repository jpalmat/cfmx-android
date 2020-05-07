package ec.com.smx.cfmx.data.api

import android.content.Context
import ec.com.smx.cfmx.data.api.exception.CustomException
import ec.com.smx.cfmx.data.api.helper.RetrofitHelper
import ec.com.smx.cfmx.data.api.listener.LocalsServiceListener
import ec.com.smx.cfmx.data.api.vo.response.LocalResponse
import ec.com.smx.kcommons.util.UNetwork
import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit
import java.net.ConnectException
import java.net.CookieManager
import java.net.HttpURLConnection

/**
 * Created by Frederick on 01/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LocalsServiceCall (context: Context) : ServiceCall(context) {

    fun getLocals(listener: LocalsServiceListener){
        val retrofitHelper = RetrofitHelper(CookieManager(), this.context)
        val httpClient = retrofitHelper.getOkHttpClient()
        val client = retrofitHelper.getRetrofitBase(this.url, httpClient)
        val service = client.create(LocalsService::class.java)
        val call = service.getLocals()
        // check available connection
        if(UNetwork.isConnected(context)){
            callGetLocals(call, listener)
        }else{
            listener.onGetLocalsFailure(ConnectException())
        }
    }

    // get locals service call
    private fun callGetLocals(call: Call<List<LocalResponse>>, listener: LocalsServiceListener) {
        call.enqueue(object : Callback<List<LocalResponse>> {
            override fun onResponse(response: Response<List<LocalResponse>>, retrofit: Retrofit?) {
                when (response.code()) {
                    HttpURLConnection.HTTP_OK -> listener.onGetLocalsResponse(response.body())
                    else -> onFailure(CustomException(response.code().toString()))
                }
            }
            override fun onFailure(t: Throwable) {
                listener.onGetLocalsFailure(t)
            }
        })
    }
}
