package ec.com.smx.cfmx.async

import android.os.AsyncTask
import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.repository.sqlite.OptionRepository
import java.util.concurrent.ExecutionException

/**
 * Created by Frederick on 08/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class UpdateFavOptionsListAsyncTask (private val appDatabase: AppDatabase, private val list: String,
                                     private val isFavorite: Boolean)
    : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg p0: Void?): Boolean {
        // Get options from params
        return try {
            val repository = OptionRepository.getInstance(appDatabase)
            repository.setFavoriteInOptionList(isFavorite, list)
            true
        }catch(e: ExecutionException){
            false
        }
    }
}
