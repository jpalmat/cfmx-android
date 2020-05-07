package ec.com.smx.cfmx.repository.sharedpreference

import android.content.SharedPreferences
import android.location.Location
import ec.com.smx.cfmx.constant.Preferences
import ec.com.smx.cfmx.data.api.vo.response.LocalResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ec.com.smx.cfmx.constant.BundleConstants

/**
 * Created by Frederick on 01/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LocalsRepository private constructor() {
    companion object {

        fun getLocalList(sharedPreferences: SharedPreferences): List<LocalResponse>{
            val list = ArrayList<LocalResponse>()
            val json = sharedPreferences.getString(Preferences.LOCAL_LIST, BundleConstants.EMPTY_JSON_ARRAY)
            val gson = GsonBuilder().setPrettyPrinting().create()
            list.addAll(gson.fromJson(  json,
                    object : TypeToken<List<LocalResponse>>() {}.type))
            return list
        }

        fun setLocalList(sharedPreferences: SharedPreferences, list: List<LocalResponse>){
            val editor = sharedPreferences.edit()
            editor.putString(Preferences.LOCAL_LIST, Gson().toJson(list))
            editor.apply()
        }

        fun getLocalById(sharedPreferences: SharedPreferences, localId: Int?): LocalResponse?{
            // get locals
            var ret: LocalResponse? = null
            if(localId != null) {
                val locals = ArrayList<LocalResponse>()
                val json = sharedPreferences.getString(Preferences.LOCAL_LIST, BundleConstants.EMPTY_JSON_ARRAY)
                val gson = GsonBuilder().setPrettyPrinting().create()
                locals.addAll(gson.fromJson(json,
                        object : TypeToken<List<LocalResponse>>() {}.type))
                // get nearest local from list

                for (each: LocalResponse in locals) {
                    if (each.id == localId) {
                        ret = each
                        break
                    }
                }
            }
            return ret
        }

        fun getNearestLocal(sharedPreferences: SharedPreferences, currentLocation: Location): LocalResponse?{
            // get locals
            val locals = ArrayList<LocalResponse>()
            val json = sharedPreferences.getString(Preferences.LOCAL_LIST, BundleConstants.EMPTY_JSON_ARRAY)
            val gson = GsonBuilder().setPrettyPrinting().create()
            locals.addAll(gson.fromJson(  json,
                    object : TypeToken<List<LocalResponse>>() {}.type))
            // get nearest local from list
            var localNearby: LocalResponse? = null
            //var coveredGpsArea = 120F
            var coveredGpsArea = BundleConstants.COVERED_GPS_AREA
            // variant local location
            val localLocation = Location(BundleConstants.LOCATION_PROVIDER)
            for (local in locals) {
                if(local.latitude!=null && local.longitude!=null) {
                    //set new location with local data
                    localLocation.latitude = local.latitude!!
                    localLocation.longitude = local.longitude!!
                    val distance = currentLocation.distanceTo(localLocation)
                    if(distance<coveredGpsArea){
                        coveredGpsArea = distance
                        localNearby = local
                    }
                }
            }
            return localNearby
        }

        fun setUserDefaultLocal(sharedPreferences: SharedPreferences, local: LocalResponse?){
            val editor = sharedPreferences.edit()
            if(local!=null){
                editor.putString(Preferences.LOCAL, Gson().toJson(local))
            }else{
                editor.putString(Preferences.LOCAL, BundleConstants.EMPTY_JSON_OBJECT)
            }
            editor.apply()
        }

        fun getUserDefaultLocal(sharedPreferences: SharedPreferences): LocalResponse?{
            val json = sharedPreferences.getString(Preferences.LOCAL, BundleConstants.EMPTY_JSON_OBJECT)
            var local: LocalResponse? = null
            if(json != BundleConstants.EMPTY_JSON_OBJECT){
                val gson = GsonBuilder().setPrettyPrinting().create()
                local = gson.fromJson(json, object : TypeToken<LocalResponse>() {}.type)
            }
            return local
        }
    }
}
