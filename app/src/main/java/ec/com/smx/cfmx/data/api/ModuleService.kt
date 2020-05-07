package ec.com.smx.cfmx.data.api

import ec.com.smx.cfmx.data.api.vo.request.AddFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.request.RemoveFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.response.DefaultResponse
import ec.com.smx.cfmx.data.api.vo.response.ModuleResponse
import org.json.JSONObject
import retrofit.Call
import retrofit.http.Body
import retrofit.http.Headers
import retrofit.http.POST

/**
 * Created by Frederick on 30/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface ModuleService {

    // Module and options service
    @Headers("Accept: application/json")
    @POST("/authProvider/menu/tree")
    fun menu(@Body emptyJson: JSONObject): Call<List<ModuleResponse>>

    @Headers("Accept: application/json")
    @POST("/authProvider/menu/addFavorite")
    fun addFavorite(@Body request: ArrayList<AddFavoriteRequest>): Call<DefaultResponse.Response>

    @Headers("Accept: application/json")
    @POST("/authProvider/menu/removeFavorite")
    fun removeFavorite(@Body request: ArrayList<RemoveFavoriteRequest>): Call<DefaultResponse.Response>

}
