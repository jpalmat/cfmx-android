package ec.com.smx.cfmx.repository.sharedpreference

import android.content.SharedPreferences
import com.google.gson.Gson
import ec.com.smx.cfmx.constant.BundleConstants
import ec.com.smx.cfmx.constant.Preferences
import ec.com.smx.cfmx.data.persistence.entity.User

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class UserRepository private constructor() {
    companion object {
        fun getUser(sharedPreferences: SharedPreferences): User {
            val json = sharedPreferences.getString(Preferences.CURRENT_USER, BundleConstants.EMPTY_JSON_OBJECT)
            return Gson().fromJson(json, User::class.java)
        }

        fun setUser(sharedPreferences: SharedPreferences, user: User) {
            val editor = sharedPreferences.edit()
            editor.putString(Preferences.CURRENT_USER, Gson().toJson(user))
            editor.apply()
        }

        fun deleteUser(sharedPreferences: SharedPreferences){
            val editor = sharedPreferences.edit()
            editor.remove(Preferences.CURRENT_USER).apply()
        }
    }
}
