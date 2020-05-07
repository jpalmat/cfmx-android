package ec.com.smx.cfmx.data.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import ec.com.smx.cfmx.data.persistence.entity.Module
import ec.com.smx.cfmx.data.persistence.relation.ModuleWithOptions

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@Dao
interface ModuleDao : BaseDao<Module> {
    @Query("SELECT * FROM modules WHERE module_id = :moduleId AND user_id = :userId")
    fun find(moduleId: Int, userId: String): Module

    @Query("SELECT * FROM modules WHERE user_id = :userId")
    fun list(userId: String): List<ModuleWithOptions>

    @Query("DELETE FROM modules WHERE user_id = :userId")
    fun deleteByUserId(userId: String)
}
