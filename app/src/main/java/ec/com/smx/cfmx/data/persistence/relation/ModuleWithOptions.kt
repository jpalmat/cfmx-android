package ec.com.smx.cfmx.data.persistence.relation

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import ec.com.smx.cfmx.data.persistence.entity.Module
import ec.com.smx.cfmx.data.persistence.entity.Option

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ModuleWithOptions {
    @Embedded
    var module: Module? = null

    @Relation(parentColumn = "module_id", entityColumn = "module_id", entity = Option::class)
    var options: List<Option>? = null
}
