package ec.com.smx.cfmx.data.api.listener

/**
 * Created by Frederick on 18/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface CheckTokenServiceListener {
    fun onCheckTokenResponse()
    fun onCheckTokenFailure(t: Throwable)
}
