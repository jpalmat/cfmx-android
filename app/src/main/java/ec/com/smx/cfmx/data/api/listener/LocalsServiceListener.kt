package ec.com.smx.cfmx.data.api.listener

import ec.com.smx.cfmx.data.api.vo.response.LocalResponse

/**
 * Created by Frederick on 01/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface LocalsServiceListener {

    fun onGetLocalsResponse(response: List<LocalResponse>?)

    fun onGetLocalsFailure(t: Throwable)
}
