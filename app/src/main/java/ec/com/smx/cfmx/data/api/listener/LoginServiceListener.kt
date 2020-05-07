package ec.com.smx.cfmx.data.api.listener

import ec.com.smx.cfmx.data.persistence.entity.User

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface LoginServiceListener {
    fun onLoginPreExecute()

    fun onLoginResponse(response: User)

    fun onLoginFailure(t: Throwable)
}
