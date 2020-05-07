package ec.com.smx.cfmx.async

import android.os.AsyncTask
import ec.com.smx.cfmx.async.listener.LoadOptionsAsyncListener
import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.entity.Option
import ec.com.smx.cfmx.repository.sqlite.OptionRepository

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LoadOptionsAsyncTask(private val appDatabase: AppDatabase,
                           private val moduleId: Int,
                           private val userId: String,
                           private val listener: LoadOptionsAsyncListener,
                           var token: String)
    : AsyncTask<Void, Void, List<Option>>() {

    override fun doInBackground(vararg params: Void?): List<Option> {
        // Get options from params
        val repository = OptionRepository.getInstance(appDatabase)
        return repository.findAllByModuleId(moduleId, userId)
    }

    override fun onPostExecute(result: List<Option>) {
        super.onPostExecute(result)
        listener.onLoadOptionsFinish(result, token)
    }
}
