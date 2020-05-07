package ec.com.smx.cfmx.async.listener

import ec.com.smx.cfmx.data.persistence.relation.ModuleWithOptions

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface LoadModuleAsyncListener {
    fun onLoadModuleFinish(list: List<ModuleWithOptions>)
}
