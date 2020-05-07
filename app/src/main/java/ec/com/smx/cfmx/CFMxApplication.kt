package ec.com.smx.cfmx

import android.app.Application
import com.crashlytics.android.Crashlytics
import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.kcommons.util.UAppExecutors
import io.fabric.sdk.android.Fabric


/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class CFMxApplication : Application() {
    private var mAppExecutors: UAppExecutors? = null

    companion object {
        val PERMISSION_ALL_MANDATORY = 103
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        mAppExecutors = UAppExecutors()
    }

    fun getDatabase(): AppDatabase {
        return AppDatabase.getInstance(this, mAppExecutors!!)
    }
}
