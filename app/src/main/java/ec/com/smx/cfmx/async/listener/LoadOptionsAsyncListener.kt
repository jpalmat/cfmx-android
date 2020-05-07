package ec.com.smx.cfmx.async.listener

import ec.com.smx.cfmx.data.persistence.entity.Option

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface LoadOptionsAsyncListener {
    fun onLoadOptionsFinish(optionList: List<Option>, token: String)
}
