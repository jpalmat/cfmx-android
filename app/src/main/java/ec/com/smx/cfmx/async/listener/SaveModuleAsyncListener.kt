package ec.com.smx.cfmx.async.listener

import ec.com.smx.cfmx.data.persistence.entity.Module

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface SaveModuleAsyncListener {
    fun onSavedModuleFinish(modules: List<Module>)
}
