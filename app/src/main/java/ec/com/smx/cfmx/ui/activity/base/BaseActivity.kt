package ec.com.smx.cfmx.ui.activity.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import ec.com.smx.cfmx.CFMxApplication
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.persistence.AppDatabase
import ec.com.smx.cfmx.data.persistence.AppSharedPreference
import ec.com.smx.cfmx.data.persistence.entity.User
import ec.com.smx.cfmx.repository.sharedpreference.UserRepository
import ec.com.smx.cfmx.ui.activity.LoginActivity
import java.net.HttpURLConnection

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
@SuppressLint("Registered")
open class BaseActivity : BaseLocationActivity() {

    internal var appDatabase: AppDatabase? = null
    lateinit var app: CFMxApplication
    lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.app = (application as CFMxApplication)
        this.appDatabase = this.app.getDatabase()
        this.currentUser = UserRepository.getUser(AppSharedPreference.getInstance(this).sharedPreferences)
    }

    fun onServiceFailure(t: Throwable) {
        if (t.message.equals(HttpURLConnection.HTTP_UNAUTHORIZED.toString())) {
            startActivity(this@BaseActivity, LoginActivity::class.java, true, null)
        }
    }

    /**
     * Navigate to another activity
     */
    fun startActivity(fromContext: Context, toContext: Class<*>, clearStack: Boolean, bundle: Bundle?) {
        val intent = Intent(fromContext, toContext)
        if (clearStack) {
            // Clear all stack for empty navigation
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        // Start activity
        startActivity(intent)
    }

    fun startApp(packageName: String, mBundle: Bundle) {
        var intent = this.packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$packageName")
        } else {
            // Put data for app here
            intent.putExtras(mBundle)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            this.startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }catch (e: ActivityNotFoundException){
            showError()
        }
    }

    open fun showError(){}

    fun hideKeyboard(){
        // hide keyboard
        try {
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }catch(e:IllegalStateException){
            Log.e(getString(R.string.no_data),getString(R.string.no_data))
        }
    }

    /*
    fun setFooterOnConstraintLayout(parent: ConstraintLayout) {
        // inflate footer
        val inflater = LayoutInflater.from(this)
        val footer = inflater.inflate(R.layout.ip_footer, null, false)
        // set it on bottom
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        footer.layoutParams = params
        parent.addView(footer)
        // set parameters on view
        val set = ConstraintSet()
        set.clone(parent)
        set.connect(footer.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        set.applyTo(parent)
    }
    */

    /**
     * Android activity lifecycle methods
     */
    override fun onResume() {
        super.onResume()
        // register gpsTracker
        enableGPSTracker()
    }

    override fun onPause() {
        super.onPause()
        // unregister gps
        disableGPSTracker()
    }
}
