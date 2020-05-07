package ec.com.smx.cfmx.repository.rest

import android.content.Context
import ec.com.smx.cfmx.data.api.LocalsServiceCall
import ec.com.smx.cfmx.data.api.listener.LocalsServiceListener

/**
 * Created by Frederick on 01/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LocalsServiceRepository private constructor() {
    companion object {
        fun getLocals(context: Context, listener: LocalsServiceListener){
            val service = LocalsServiceCall(context)
            service.getLocals(listener)
        }
    }
}
