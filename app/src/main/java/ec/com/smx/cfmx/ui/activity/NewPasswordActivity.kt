package ec.com.smx.cfmx.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.constant.BundleConstants
import ec.com.smx.cfmx.data.api.listener.ChangePasswordServiceListener
import ec.com.smx.cfmx.data.api.listener.LoginServiceListener
import ec.com.smx.cfmx.data.api.vo.request.ChangePasswordRequest
import ec.com.smx.cfmx.data.api.vo.request.LoginRequest
import ec.com.smx.cfmx.data.api.vo.response.ChangePasswordResponse
import ec.com.smx.cfmx.data.persistence.AppSharedPreference
import ec.com.smx.cfmx.data.persistence.entity.User
import ec.com.smx.cfmx.repository.rest.AccountServiceRepository
import ec.com.smx.cfmx.repository.sharedpreference.LocalsRepository
import ec.com.smx.cfmx.repository.sharedpreference.UserRepository
import ec.com.smx.kcommons.util.UMessage
import ec.com.smx.kcommonsencrypt.UEncryption
import kotlinx.android.synthetic.main.activity_new_password.*
import kotlinx.android.synthetic.main.app_bar_main.*

/**
 * Created by Frederick on 19/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class NewPasswordActivity : AppCompatActivity(),
                            ChangePasswordServiceListener,
                            LoginServiceListener {

    private lateinit var viewContent: View
    private var currentUser = User()
    private var fromLogin : Boolean = false

    /** LOGIN LISTENERS
     */
    override fun onLoginPreExecute() {}

    override fun onLoginResponse(response: User) {
        // set user default local
        val pref = AppSharedPreference.getInstance(this).sharedPreferences
        val local = LocalsRepository.getLocalById(pref, response.defaultLocalId)
        LocalsRepository.setUserDefaultLocal(pref, local)
        // set user data in preference
        UserRepository.setUser(pref, response)
        UMessage.showToast(this, getString(R.string.toast_change_password_success))

        if(fromLogin) {
            // from login because password was expired
            // Go to main activity
            this.startActivityMethod(this, MainActivity::class.java, true, null)
        }else{
            // from main -> change password
            finish()
        }
    }

    override fun onLoginFailure(t: Throwable) {
        // in this case change password method was success but background login get a error..
        // so app redirect to login activity and notify that password was changed successfully
        UMessage.showToast(this, getString(R.string.change_user_relogin))

        if(fromLogin) {
            // from login because password was expired
            // Go to main activity
            startActivityMethod(this, LoginActivity::class.java, true, null)
        }else{
            // from main -> change password
            finish()
        }
    }

    /** CHANGE PASSWORD LISTENERS
     */
    override fun onChangePassResponse(response: ChangePasswordResponse) {
        if(response.status!=null && response.status!!) {
            // change progress message
            this.changeUserProgressBarText.text = getString(R.string.login_progress_text)
            // Call login ws
            val request = LoginRequest()
            request.userName = currentUser.userName
            request.password = this.changeUserNewPassTv.text.toString()
            AccountServiceRepository.login(this, request, this)
        }else{
            this.changeUserProgressBar.visibility = View.GONE
            this.btnLay.visibility = View.VISIBLE
            enableFormFields()
            if(response.message!=null) {
                UMessage.showSnackBar(viewContent, this, response.message!!)
            }else{
                UMessage.showSnackBar(viewContent, this, R.string.change_user_error_bundle)
            }
        }
    }

    override fun onChangePassFailure(t: Throwable) {
        // hide progressbar
        this.changeUserProgressBar.visibility = View.GONE
        this.btnLay.visibility = View.VISIBLE
        enableFormFields()
        if(t.message != null) {
            UMessage.showSnackBar(viewContent, this, t.message.toString())
        }else{
            UMessage.showSnackBar(viewContent, this, R.string.change_user_error_bundle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)
        this.initializeComponents()
    }

    private fun initializeComponents() {
        // set view for snackbar
        viewContent = findViewById(android.R.id.content)
        // Setup toolbar
        setSupportActionBar(toolbar)
        // get parameters
        getBundle()
    }

    @Suppress("UNUSED_PARAMETER")
    fun doChangePasswordOnClick(view: View){
        if (this.isValidForm()) {
            disableFormFields()
            this.changeUserProgressBar.visibility = View.VISIBLE
            this.changeUserProgressBarText.text = getString(R.string.change_user_progress_text)
            this.btnLay.visibility = View.GONE
            // call ws
            val request = ChangePasswordRequest()
            request.userId = currentUser.userId
            request.oldPassword = this.changeUserOldPassTv.text.toString()
            request.newPassword = this.changeUserNewPassTv.text.toString()

            if(currentUser.token==null){
                setErrorInBundle()
            }else {
                AccountServiceRepository.changePass(this, currentUser.token!!, request, this)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun cancelChangePasswordOnClick(view: View){
        super.onBackPressed()
    }

    private fun disableFormFields(){
        this.changeUserOldPassTv.isEnabled = false
        this.changeUserNewPassTv.isEnabled = false
        this.changeUserConfirmNewPassTv.isEnabled = false
    }

    private fun enableFormFields(){
        this.changeUserOldPassTv.isEnabled = true
        this.changeUserNewPassTv.isEnabled = true
        this.changeUserConfirmNewPassTv.isEnabled = true
    }

    private fun getBundle(){
        val extras = intent.extras
        if (extras != null && extras.containsKey(BundleConstants.FROM_LOGIN_ACTIVITY_BUNDLE)) {
            if(extras.getBoolean(BundleConstants.FROM_LOGIN_ACTIVITY_BUNDLE)){
                // this activity was called from login activity
                if(extras.containsKey(BundleConstants.USER_BUNDLE)){
                    currentUser = extras.getParcelable<User>(BundleConstants.USER_BUNDLE)
                    this.changeUserOldPassLabel.visibility = View.GONE
                    this.changeUserOldPassLabel.hint = getString(R.string.change_user_temp_pass)
                    this.changeUserPassDescription.text = getString(R.string.change_user_description_forgotpass)
                    // set password edit text
                    this.changeUserOldPassTv.setText(UEncryption.decrypt(currentUser.password))
                    this.changeUserOldPassTv.isEnabled = false
                    // from login
                    fromLogin = true
                }else{
                    // show error message
                    setErrorInBundle()
                }
            }else{
                // this activity was called from main activity
                currentUser = UserRepository.
                        getUser(AppSharedPreference.getInstance(this).sharedPreferences)
                this.changeUserOldPassLabel.hint = getString(R.string.change_user_old_pass)
                this.changeUserPassDescription.text = getString(R.string.change_user_description_changepass)
                fromLogin = false
            }
        }else{
            // show error message
            setErrorInBundle()
        }
    }

    private fun isValidForm(): Boolean {
        return when {
            this.changeUserOldPassTv.text.isEmpty() -> {
                this.changeUserOldPassTv.requestFocus()
                this.changeUserOldPassTv.error = getString(R.string.required_field)
                false
            }
            this.changeUserNewPassTv.text.isEmpty() -> {
                this.changeUserNewPassTv.requestFocus()
                this.changeUserNewPassTv.error = getString(R.string.required_field)
                false
            }
            this.changeUserConfirmNewPassTv.text.isEmpty() -> {
                this.changeUserConfirmNewPassTv.requestFocus()
                this.changeUserConfirmNewPassTv.error = getString(R.string.required_field)
                false
            }
            this.changeUserOldPassTv.text.toString() == this.changeUserNewPassTv.text.toString() -> {
                this.changeUserNewPassTv.requestFocus()
                this.changeUserOldPassTv.error = getString(R.string.change_user_not_equal_field)
                this.changeUserNewPassTv.error = getString(R.string.change_user_not_equal_field)
                false
            }
            this.changeUserNewPassTv.text.toString() != this.changeUserConfirmNewPassTv.text.toString() -> {
                this.changeUserNewPassTv.requestFocus()
                this.changeUserNewPassTv.error = getString(R.string.change_user_new_different_password)
                this.changeUserConfirmNewPassTv.error = getString(R.string.change_user_new_different_password)
                false
            }
            else -> true
        }
    }

    private fun setErrorInBundle(){
        // show error message
        UMessage.showToast(this, getString(R.string.change_user_error_bundle))
        // go back to login
        startActivityMethod(this, LoginActivity::class.java, true, null)
        finish()
    }

    private fun startActivityMethod(fromContext: Context,
                                    toContext: Class<*>,
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

}
