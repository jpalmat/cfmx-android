package ec.com.smx.cfmx.data.persistence

import android.content.Context
import android.content.SharedPreferences
import ec.com.smx.cfmx.constant.Preferences

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class AppSharedPreference private constructor(context: Context) {

    internal var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME,
            Context.MODE_PRIVATE)

    companion object {
        const val PREFERENCES_NAME = "master_shared_preferences"
        private var mInstance: AppSharedPreference? = null
        fun getInstance(context: Context): AppSharedPreference {
            if (mInstance == null) {
                mInstance = AppSharedPreference(context)
            }
            return mInstance as AppSharedPreference
        }
    }

    /**
     * Clear all data saved on preferences except locals
     */
    fun logoutClearPreference() {
        val editor = sharedPreferences.edit()
        editor.remove(Preferences.CURRENT_USER)
        editor.remove(Preferences.LOCAL)
        editor.apply()
    }

    fun clearPreferenceFavorites() {
        val editor = sharedPreferences.edit()
        editor.remove(Preferences.FAV)
        editor.apply()
    }
}
