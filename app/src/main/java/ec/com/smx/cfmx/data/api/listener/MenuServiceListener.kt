package ec.com.smx.cfmx.data.api.listener

import ec.com.smx.cfmx.data.api.vo.response.ModuleResponse

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface MenuServiceListener {
    fun onMenuResponse(response: List<ModuleResponse>?)

    fun onMenuFailure(t: Throwable)
}
