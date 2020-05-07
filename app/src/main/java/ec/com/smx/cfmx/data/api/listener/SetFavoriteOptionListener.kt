package ec.com.smx.cfmx.data.api.listener

import ec.com.smx.cfmx.data.api.vo.request.AddFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.request.RemoveFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.response.DefaultResponse

/**
 * Created by Frederick on 30/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface SetFavoriteOptionListener {

    fun onSetFavoriteOptionResponse(response: DefaultResponse.Response?, request:  ArrayList<AddFavoriteRequest>)
    fun onSetFavoriteOptionFailure(t: Throwable, request: ArrayList<AddFavoriteRequest>)

    fun onRemoveFavoriteOptionResponse(response: DefaultResponse.Response?, request: ArrayList<RemoveFavoriteRequest>)
    fun onRemoveFavoriteOptionFailure(t: Throwable, request: ArrayList<RemoveFavoriteRequest>)
}
