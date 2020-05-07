package ec.com.smx.cfmx.repository.rest

import android.content.Context
import ec.com.smx.cfmx.data.api.ModuleServiceCall
import ec.com.smx.cfmx.data.api.listener.MenuServiceListener
import ec.com.smx.cfmx.data.api.listener.SetFavoriteOptionListener
import ec.com.smx.cfmx.data.api.vo.request.AddFavoriteRequest
import ec.com.smx.cfmx.data.api.vo.request.RemoveFavoriteRequest

/**
 * Created by Frederick on 30/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ModulesServiceRepository private constructor() {
    companion object {

        fun menu(context: Context, token: String, listener: MenuServiceListener) {
            val service = ModuleServiceCall(context)
            service.menu(token, listener)
        }

        fun addFavorite(context: Context, request:  ArrayList<AddFavoriteRequest>,
                        token: String, listener: SetFavoriteOptionListener) {
            // get token
            val service = ModuleServiceCall(context)
            service.addFavorite(request, token, listener)
        }

        fun removeFavorite(context: Context, request: ArrayList<RemoveFavoriteRequest>,
                           token: String, listener: SetFavoriteOptionListener) {
            // get token
            val service = ModuleServiceCall(context)
            service.removeFavorite(request, token, listener)
        }

    }
}
