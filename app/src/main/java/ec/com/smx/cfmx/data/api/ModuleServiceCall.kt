package ec.com.smx.cfmx.data.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.api.exception.CustomException
import ec.com.smx.cfmx.data.api.listener.MenuServiceListener
import ec.com.smx.cfmx.data.api.listener.SetFavoriteOptionListener
import ec.com.smx.cfmx.data.api.vo.request.AddFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.request.RemoveFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.response.DefaultResponse
import ec.com.smx.cfmx.data.api.vo.response.ModuleResponse
import ec.com.smx.kcommons.util.UNetwork
import org.json.JSONObject
import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit
import java.net.ConnectException
import java.net.HttpURLConnection

/**
 * Created by Frederick on 30/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ModuleServiceCall(context: Context) : ServiceCall(context) {

    // get modules web services
    fun menu(token: String, listener: MenuServiceListener) {
        val client = this.getClientWithHeader(this.url, token)
        val service = client.create(ModuleService::class.java)
        val call = service.menu(JSONObject())
        // check available connection
        if(UNetwork.isConnected(context)){
            callMenu(call, listener)
        }else{
            listener.onMenuFailure(ConnectException(""))
        }
    }

    // menu service call
    private fun callMenu(call: Call<List<ModuleResponse>>, listener: MenuServiceListener) {
        call.enqueue(object : Callback<List<ModuleResponse>> {
            override fun onResponse(response: Response<List<ModuleResponse>>, retrofit: Retrofit?) {
                when (response.code()) {
                    HttpURLConnection.HTTP_OK -> listener.onMenuResponse(response.body())
                    else -> onFailure(CustomException(response.code().toString()))
                }
            }
            override fun onFailure(t: Throwable) {
                listener.onMenuFailure(t)
            }
        })
    }

    // add favorite option
    fun addFavorite(request: ArrayList<AddFavoriteRequest>,
                    token: String,
                    listener: SetFavoriteOptionListener) {
        val client = this.getClientWithHeader(this.url, token)
        val service = client.create(ModuleService::class.java)
        val call = service.addFavorite(request)
        // check available connection
        if(UNetwork.isConnected(context)){
            callAddFavorite(call, listener, request)
        }else{
            listener.onSetFavoriteOptionFailure(ConnectException(""), request)
        }
    }

    // add favorite service call
    private fun callAddFavorite(call: Call<DefaultResponse.Response>,
                                listener: SetFavoriteOptionListener,
                                request: ArrayList<AddFavoriteRequest>) {
        call.enqueue(object : Callback<DefaultResponse.Response> {
            override fun onResponse(response: Response<DefaultResponse.Response>, retrofit: Retrofit?) {
                when (response.code()) {
                    HttpURLConnection.HTTP_OK ->
                        listener.onSetFavoriteOptionResponse(response.body(), request)
                    HttpURLConnection.HTTP_BAD_REQUEST ->{
                        val json = response.errorBody().string()
                        try {
                            val obj = Gson().fromJson(json, DefaultResponse::class.java)
                            if(obj.response!=null){
                                this.onFailure(CustomException(obj.response!!.message))
                            }else{
                                this.onFailure(CustomException(context.getString(R.string.ws_error,
                                        HttpURLConnection.HTTP_CONFLICT)))
                            }
                        }catch(e: JsonSyntaxException){
                            this.onFailure(CustomException(context.getString(R.string.ws_error,
                                    HttpURLConnection.HTTP_CONFLICT)))
                        }
                    }
                    else -> onFailure(CustomException(
                            context.getString(R.string.snackbar_error_store_favorite,response.code().toString())))
                }
            }
            override fun onFailure(t: Throwable) {
                listener.onSetFavoriteOptionFailure(t, request)
            }
        })
    }

    // remove favorite module from favorite container
    fun removeFavorite(request: ArrayList<RemoveFavoriteRequest>,
                       token: String,
                       listener: SetFavoriteOptionListener) {
        val client = this.getClientWithHeader(this.url, token)
        val service = client.create(ModuleService::class.java)
        val call = service.removeFavorite(request)
        // check available connection
        if(UNetwork.isConnected(context)){
            callRemoveFavorite(call, listener, request)
        }else{
            listener.onRemoveFavoriteOptionFailure(ConnectException(""), request)
        }
    }

    // remove favorite service call
    private fun callRemoveFavorite(call: Call<DefaultResponse.Response>,
                                   listener: SetFavoriteOptionListener,
                                   request: ArrayList<RemoveFavoriteRequest>) {
        call.enqueue(object : Callback<DefaultResponse.Response> {
            override fun onResponse(response: Response<DefaultResponse.Response>, retrofit: Retrofit?) {
                // check response
                when (response.code()) {
                    HttpURLConnection.HTTP_OK ->
                        listener.onRemoveFavoriteOptionResponse(response.body(), request)
                    else -> onFailure(CustomException(response.code().toString()))
                }
            }
            override fun onFailure(t: Throwable) {
                listener.onRemoveFavoriteOptionFailure(t, request)
            }
        })
    }
}
