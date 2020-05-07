package ec.com.smx.cfmx.async

import android.os.AsyncTask
import ec.com.smx.cfmx.async.listener.LoadModuleAsyncListener
import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.relation.ModuleWithOptions
import ec.com.smx.cfmx.repository.sqlite.ModuleRepository

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LoadModuleAsyncTask(private val appDatabase: AppDatabase, private val listener: LoadModuleAsyncListener,
                          private val userId: String)
    : AsyncTask<Void, Void, List<ModuleWithOptions>>() {

    override fun doInBackground(vararg params: Void?): List<ModuleWithOptions> {
        val repository = ModuleRepository.getInstance(appDatabase)
        return repository.list(userId)
    }

    override fun onPostExecute(result: List<ModuleWithOptions>) {
        super.onPostExecute(result)
        listener.onLoadModuleFinish(result)
    }
}
