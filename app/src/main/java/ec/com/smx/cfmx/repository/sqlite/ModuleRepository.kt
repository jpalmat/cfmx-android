package ec.com.smx.cfmx.repository.sqlite

import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.entity.Module
import ec.com.smx.cfmx.data.persistence.relation.ModuleWithOptions
import org.apache.commons.collections4.CollectionUtils

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ModuleRepository private constructor(private val mDatabase: AppDatabase) {

    companion object {

        private var mInstance: ModuleRepository? = null

        fun getInstance(database: AppDatabase): ModuleRepository {
            if (mInstance == null) {
                synchronized(ModuleRepository::class.java) {
                    if (mInstance == null) {
                        mInstance = ModuleRepository(database)
                    }
                }
            }
            return mInstance!!
        }
    }

    /**
     * Get a module from the database
     */
    fun find(moduleId: Int, userId: String): Module {
        return mDatabase.moduleDao().find(moduleId, userId)
    }

    /**
     * Get the list of modules from the database
     */
    fun list(userId: String): List<ModuleWithOptions> {
        return mDatabase.moduleDao().list(userId)
    }

    /**
     *  Save all modules for current user
     */
    fun persistModule(moduleList: List<Module>, userId: String) {
        // Delete all user options
        mDatabase.optionDao().deleteByUserId(userId)
        // Delete all user modules
        mDatabase.moduleDao().deleteByUserId(userId)
        // Insert all modules for current user
        mDatabase.moduleDao().insertAll(moduleList)
        for (module in moduleList) {
            if (CollectionUtils.isNotEmpty(module.options)) {
                // Insert all options from module and current user
                mDatabase.optionDao().insertAll(module.options!!)
            }

        }
    }
}
