package ec.com.smx.cfmx.async

import android.os.AsyncTask
import ec.com.smx.cfmx.async.listener.SaveModuleAsyncListener
import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.entity.Module
import ec.com.smx.cfmx.repository.sqlite.ModuleRepository

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class SaveModuleAsyncTask(private val appDatabase: AppDatabase, private val listener: SaveModuleAsyncListener,
                          private val modules: List<Module>, private val userId: String)
    : AsyncTask<String, Void, Void>() {
    override fun doInBackground(vararg params: String?): Void? {
        val repository = ModuleRepository.getInstance(this.appDatabase)
        repository.persistModule(modules, userId)
        return null
    }


    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        // Notify callback
        listener.onSavedModuleFinish(modules)
    }
}
