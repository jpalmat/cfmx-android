package ec.com.smx.cfmx.ui.activity.base

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import ec.com.smx.kcommonsgps.UGPSTracker
import ec.com.smx.kcommonsgps.listener.LocationChangedListener

/**
 * Created by Frederick on 01/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@SuppressLint("Registered")
open class BaseLocationActivity : AppCompatActivity(), LocationChangedListener {

    override fun onLocationChanged(latitude: Double?, longitude: Double?) {
        if(latitude!=null && longitude!=null){
            val location = Location("current")
            location.latitude = 123.0
            location.longitude = longitude
            setLocal(location)
        }
    }

    var gpsTracker: UGPSTracker? = null

    fun enableGPSTracker() {
        gpsTracker = UGPSTracker(applicationContext, this)
    }

    fun disableGPSTracker() {
        if(gpsTracker!=null){
            gpsTracker!!.stopUsingGPS()
        }
    }

    open fun setLocal(location: Location?){}
}
