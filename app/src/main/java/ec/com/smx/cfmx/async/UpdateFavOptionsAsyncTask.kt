package ec.com.smx.cfmx.async

import android.os.AsyncTask
import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.entity.Option
import ec.com.smx.cfmx.async.listener.UpdateOptionsAsyncListener
import ec.com.smx.cfmx.repository.sqlite.OptionRepository
import java.util.concurrent.ExecutionException

/**
 * Created by Frederick on 03/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class UpdateFavOptionsAsyncTask (private val appDatabase: AppDatabase, private val option: Option,
                                 private val userId: String,
                                 private val listener: UpdateOptionsAsyncListener)
    : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg p0: Void?): Boolean {
        // Get options from params
        return try {
            val repository = OptionRepository.getInstance(appDatabase)
            repository.setFavoriteInOption(option, userId)
            true
        }catch(e: ExecutionException){
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        if(!result){
            listener.onUpdateOptionsFail()
        }
    }
}
