package ec.com.smx.cfmx.data.api

import ec.com.smx.cfmx.data.api.vo.response.LocalResponse
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Headers

/**
 * Created by Frederick on 01/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface LocalsService {
    // Module and options service
    @Headers("Accept: application/json")
    @GET("/authProvider/local")
    fun getLocals(): Call<List<LocalResponse>>
}
