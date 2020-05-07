package ec.com.smx.cfmx.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import ec.com.smx.cfmx.CFMxApplication
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.api.exception.CustomException
import ec.com.smx.cfmx.data.api.listener.CheckTokenServiceListener
import ec.com.smx.cfmx.data.api.listener.LocalsServiceListener
import ec.com.smx.cfmx.data.api.listener.LoginServiceListener
import ec.com.smx.cfmx.data.api.vo.request.LoginRequest
import ec.com.smx.cfmx.data.api.vo.response.LocalResponse
import ec.com.smx.cfmx.data.persistence.AppSharedPreference
import ec.com.smx.cfmx.data.persistence.entity.User
import ec.com.smx.cfmx.repository.rest.AccountServiceRepository
import ec.com.smx.cfmx.repository.rest.LocalsServiceRepository
import ec.com.smx.cfmx.repository.sharedpreference.LocalsRepository
import ec.com.smx.cfmx.repository.sharedpreference.UserRepository
import ec.com.smx.kcommons.util.UMessage
import ec.com.smx.kcommonsencrypt.UEncryption
import org.apache.commons.collections4.CollectionUtils
import java.net.ConnectException
import java.net.HttpURLConnection
import ec.com.smx.cfmx.repository.sharedpreference.FavRepository
import ec.com.smx.kcommonsupdate.UUpdate
import ec.com.smx.kcommonsupdate.api.listeners.ApplicationUpdateListener
import ec.com.smx.kcommonsupdate.constants.AppUpdateStatus

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class SplashActivity :  AppCompatActivity(),
                        CheckTokenServiceListener,
                        LocalsServiceListener,
                        LoginServiceListener,
                        ApplicationUpdateListener{

    private lateinit var viewContent: View
    private var update: UUpdate? = null

    override fun onApplicationUpdateResponse(response: AppUpdateStatus?) {
        when(response){
            AppUpdateStatus.ERROR_UPDATING->{
                Toast.makeText(this, this.getString(R.string.application_update_error), Toast.LENGTH_SHORT).show()
                this.getAsyncData()
            }
            AppUpdateStatus.UPDATED->{
                finish()
            }
            else -> {
                this.getAsyncData()
            }
        }
    }

    /*
     * Re-Login
     */
    override fun onLoginPreExecute() {}

    override fun onLoginResponse(response: User) {
        // set user default local
        val pref = AppSharedPreference.getInstance(this).sharedPreferences
        val local = LocalsRepository.getLocalById(pref, response.defaultLocalId)
        LocalsRepository.setUserDefaultLocal(pref, local)
        // set user data in preference
        UserRepository.setUser(pref, response)
        // Go to main activity and finish this one
        this.startActivity(this, MainActivity::class.java, true, null)
    }

    override fun onLoginFailure(t: Throwable) {
        // go to login view
        goToLogin()
    }

    /*
     * check valid token response listener
     */
    override fun onCheckTokenResponse() {
        this.startActivity(this@SplashActivity, MainActivity::class.java, true, null)
        finish()
    }

    override fun onCheckTokenFailure(t: Throwable) {
        when(t){
            // set snack bar message with error
            is CustomException -> doReLogin()
            else -> {
                // go to login view
                goToLogin()
            }
        }
    }

    /*
     * get locals service response listener
     */
    override fun onGetLocalsResponse(response: List<LocalResponse>?) {
        if(response!=null && CollectionUtils.isNotEmpty(response)) {
            // store locals
            LocalsRepository.setLocalList(AppSharedPreference.getInstance(this).sharedPreferences, response)
            // check logged user
            this.checkUserLogged()
        }else{
            showSnackBarWithOption(getString(R.string.empty_response))
        }
    }

    override fun onGetLocalsFailure(t: Throwable) {
        when(t){
            // set snack bar message with error
            is CustomException -> showSnackBarWithOption(t.message!!)
            is ConnectException -> showSnackBarWithOption(getString(R.string.no_internet))
            else -> showSnackBarWithOption(HttpURLConnection.HTTP_INTERNAL_ERROR.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        this.initializeComponents()
        this.checkPermissions()
    }

    private fun updateApp(){
        this.update =  UUpdate(this,getString(R.string.devsu_apk_url) ,this as ApplicationUpdateListener)
        this.update?.updateApp()
    }

    private fun showSnackBarWithOption(exceptionType: String){
        val snackBar = Snackbar
                .make(  viewContent,
                        getString(R.string.snackbar_error_get_locals,exceptionType),
                        Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .setAction(getString(R.string.snackbar_retry)) {
                    getLocalsAsync()
                }
        // set snack background
        val snackBarView = snackBar.view
        // set snack message
        val tv = snackBarView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        snackBar.show()
    }

    private fun doReLogin(){
        val currentUser = UserRepository.getUser(AppSharedPreference.getInstance(this).sharedPreferences)
        val request = LoginRequest()
        request.userName = currentUser.userName
        request.password = UEncryption.decrypt(currentUser.password)
        AccountServiceRepository.login(this, request, this)
    }

    private fun getAsyncData(){
        getLocalsAsync()
    }

    private fun getLocalsAsync(){
        // Call ws to get locals
        LocalsServiceRepository.getLocals(this, this)
    }

    private fun initializeComponents() {
        viewContent = findViewById(android.R.id.content)
    }

    private fun checkUserLogged(){
        val currentUser = UserRepository.getUser(AppSharedPreference.getInstance(this).sharedPreferences)
        if (currentUser.token==null) {
            // go to login view
            goToLogin()
        }else{
            checkValidTokenAsync(currentUser.token!!)
        }
    }

    private fun checkValidTokenAsync(token: String) {
        // Call ws to check token
        AccountServiceRepository.checkToken(this, token, this)
    }

    /**
     * Navigate to another activity
     */
    private fun startActivity(fromContext: Context, toContext: Class<*>,
                              clearStack: Boolean, bundle: Bundle?) {
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

    /*
     * PERMISSIONS
     */
    private val isAccessFinePermissionMandatoryGranted: Boolean
        get() = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /*
     *  LIST PERMISSIONS
     */
    private fun setPermissionMandatory() {
        val permissions = arrayOf(  Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, CFMxApplication.PERMISSION_ALL_MANDATORY)
    }

    private fun checkPermissions(){
        if (this.isAccessFinePermissionMandatoryGranted) {
            updateApp()
        } else {
            // ask for permissions
            this.setPermissionMandatory()
        }
    }

    private fun goToLogin(){
        // delete user pref data
        UserRepository.deleteUser(AppSharedPreference.getInstance(this).sharedPreferences)
        FavRepository.deleteFavs(AppSharedPreference.getInstance(this).sharedPreferences)
        // go to login activity
        this.startActivity(this@SplashActivity, LoginActivity::class.java, true, null)
    }

    /*
     *  ON RESPONSE PERMISSIONS ASK
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            UUpdate.WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //
                    // Permission was granted, yay! Do the
                    // Storage task you need to do.
                    // Call to start download apk from url
                    this.update?.downloadAPK()
                } else {
                    // Permission denied, boo! Disable the
                    // Functionality that depends on this permission.
                    Toast.makeText(this, "No es posible actualizar la app sin permisos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else -> {
                var flag = true
                var errorCause = getString(R.string.permission_denied) + " "
                for (i in permissions.indices) {
                    if (!(grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                        if (permissions[i] == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                            errorCause += getString(R.string.gps_permission_denied)
                        }
                        flag = false
                    }
                }
                if (!flag) {
                    UMessage.showToast(applicationContext, errorCause, Toast.LENGTH_LONG)
                    finish()
                }else{
                    updateApp()
                }
            }
        }
    }
}
