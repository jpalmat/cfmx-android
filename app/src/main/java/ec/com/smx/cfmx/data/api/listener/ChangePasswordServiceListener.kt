package ec.com.smx.cfmx.data.api.listener

import ec.com.smx.cfmx.data.api.vo.response.ChangePasswordResponse

/**
 * Created by Frederick on 19/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface ChangePasswordServiceListener {
    fun onChangePassResponse(response: ChangePasswordResponse)
    fun onChangePassFailure(t: Throwable)
}
