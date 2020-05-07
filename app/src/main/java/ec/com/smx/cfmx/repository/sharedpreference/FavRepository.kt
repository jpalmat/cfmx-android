package ec.com.smx.cfmx.repository.sharedpreference

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ec.com.smx.cfmx.constant.BundleConstants
import ec.com.smx.cfmx.constant.Preferences

/**
 * Created by Frederick on 03/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class FavRepository private constructor() {
    companion object {
        fun getFavList(sharedPreferences: SharedPreferences): List<Long>{
            val list = ArrayList<Long>()
            val json = sharedPreferences.getString(Preferences.FAV, BundleConstants.EMPTY_JSON_ARRAY)
            val gson = GsonBuilder().setPrettyPrinting().create()
            list.addAll(gson.fromJson(  json,
                    object : TypeToken<List<Long>>() {}.type))
            return list
        }

        fun addFavList(sharedPreferences: SharedPreferences, item: Long){
            val editor = sharedPreferences.edit()
            // get fav
            val list = ArrayList<Long>()
            val json = sharedPreferences.getString(Preferences.FAV, BundleConstants.EMPTY_JSON_ARRAY)
            val gson = GsonBuilder().setPrettyPrinting().create()
            list.addAll(gson.fromJson(  json,
                    object : TypeToken<List<Long>>() {}.type))
            if(list.indexOf(item)==-1){
                list.add(item)
            }
            editor.putString(Preferences.FAV, Gson().toJson(list))
            editor.apply()
        }

        fun removeFav(sharedPreferences: SharedPreferences, itemsToDeleteList: ArrayList<Int>){
            val editor = sharedPreferences.edit()
            // get fav
            val list = ArrayList<Long>()
            val json = sharedPreferences.getString(Preferences.FAV, BundleConstants.EMPTY_JSON_ARRAY)
            val gson = GsonBuilder().setPrettyPrinting().create()
            list.addAll(gson.fromJson(  json,
                    object : TypeToken<List<Long>>() {}.type))
            for(item: Int in itemsToDeleteList){
                list.removeAt(item)
            }
            editor.putString(Preferences.FAV, Gson().toJson(list))
            editor.apply()
        }

        fun deleteFavs(sharedPreferences: SharedPreferences){
            val editor = sharedPreferences.edit()
            editor.remove(Preferences.FAV).apply()
        }
    }
}
