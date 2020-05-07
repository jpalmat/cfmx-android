package ec.com.smx.cfmx.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.api.listener.LoginServiceListener
import ec.com.smx.cfmx.data.api.vo.request.LoginRequest
import ec.com.smx.cfmx.data.persistence.AppSharedPreference
import ec.com.smx.cfmx.data.persistence.entity.User
import ec.com.smx.cfmx.repository.rest.AccountServiceRepository
import ec.com.smx.cfmx.repository.sharedpreference.LocalsRepository
import ec.com.smx.cfmx.repository.sharedpreference.UserRepository
import ec.com.smx.cfmx.ui.activity.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login_forgot_pass.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import ec.com.smx.cfmx.data.api.listener.ForgotPasswordListener
import ec.com.smx.cfmx.data.api.vo.request.ForgotPassUsernameRequest
import ec.com.smx.cfmx.data.api.vo.response.CheckUsernameResponse
import android.text.Editable
import android.text.TextWatcher
import ec.com.smx.cfmx.constant.BundleConstants
import ec.com.smx.cfmx.data.api.exception.CustomException
import ec.com.smx.cfmx.data.api.vo.request.ForgotPassQuestionsRequest
import ec.com.smx.cfmx.data.api.vo.response.ForgotPassQuestionsResponse
import ec.com.smx.kcommons.util.UMessage
import java.net.ConnectException
import java.net.HttpURLConnection
import android.view.ViewGroup
import android.support.constraint.ConstraintSet

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LoginActivity : BaseActivity(), LoginServiceListener, ForgotPasswordListener {

    private lateinit var view: View
    private lateinit var viewContent: View
    private var userData: CheckUsernameResponse? = null
    private var doubleBackToExitPressedOnce = false
    private var currentQuestion = CheckUsernameResponse.QuestionsResponse()

    override fun onCheckUsernameResponse(response: CheckUsernameResponse?) {
        view.forgot_pass_progressbar.visibility = View.GONE
        if(response != null) {
            onUsernameResponse(response)
        }else{
            view.forgot_pass_username_lay.visibility = View.VISIBLE
            view.forgot_pass_username_error.visibility = View.VISIBLE
            view.forgot_pass_username_error_text.text =
                    getString(R.string.forgot_password_dialog_username_error)
        }
    }

    override fun onSendSecurityQuestionAnswerResponse(response: ForgotPassQuestionsResponse?) {
        view.forgot_pass_progressbar.visibility = View.GONE
        if(response?.status != null && response.status!!) {
            view.forgot_pass_question_lay.visibility = View.GONE
            view.forgot_pass_question_error.visibility = View.GONE
            view.forgot_pass_message.text =
                    getString(R.string.forgot_password_dialog_success,
                            if(userData!!.emailEmpresarial==null){
                                if(userData!!.emailPersonal==null) {
                                    // no email to send data
                                    getString(R.string.no_data)
                                }else{
                                    userData!!.emailPersonal
                                }
                            }else userData!!.emailEmpresarial)
            view.forgot_pass_message_lay.visibility = View.VISIBLE
        }else{
            view.forgot_pass_question_lay.visibility = View.VISIBLE
            view.forgot_pass_question_error.visibility = View.VISIBLE
        }
    }

    override fun onForgotPasswordFailure(t: Throwable, type: Int) {
        view.forgot_pass_progressbar.visibility = View.GONE
        when(type) {
            0 -> {
                val msg: String =
                if(t is CustomException) {
                    t.message!!
                }else{
                    getString(R.string.ws_error, type)
                }
                view.forgot_pass_username_lay.visibility = View.VISIBLE
                view.forgot_pass_username_error.visibility = View.VISIBLE
                view.forgot_pass_username_error_text.text = msg
            }
            1 -> {
                view.forgot_pass_question_lay.visibility = View.VISIBLE
                view.forgot_pass_question_error.visibility = View.VISIBLE
            }
        }

    }

    override fun onLoginPreExecute() {
        hideKeyboard()
        // Disable UI
        this.inputPassword.isEnabled = false
        this.inputUser.isEnabled = false
        this.btnLogin.isEnabled = false
        this.loadingProgressBar.visibility = View.VISIBLE
    }

    override fun onLoginResponse(response: User) {
        if(response.changePwd){
            // Password expire
            // Go to change password activity
            val bundle = Bundle()
            bundle.putParcelable(BundleConstants.USER_BUNDLE, response)
            bundle.putBoolean(BundleConstants.FROM_LOGIN_ACTIVITY_BUNDLE, true)
            this.startActivity(this, NewPasswordActivity::class.java, false, bundle)
            // Restore UI
            this.loadingProgressBar.visibility = View.GONE
            this.btnLogin.isEnabled = true
            this.inputPassword.isEnabled = true
            this.inputUser.isEnabled = true

        }else {
            // set user default local
            val pref = AppSharedPreference.getInstance(this).sharedPreferences
            val local = LocalsRepository.getLocalById(pref, response.defaultLocalId)
            LocalsRepository.setUserDefaultLocal(pref, local)
            // set user data in preference
            UserRepository.setUser(pref, response)
            // Go to main activity and finish this one
            this.startActivity(this, MainActivity::class.java, true, null)
        }
    }

    override fun onLoginFailure(t: Throwable) {
        // error message
        val message = when(t){
            is CustomException -> t.message
            is java.net.SocketTimeoutException -> getString(R.string.ws_error, HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
            is ConnectException -> getString(R.string.snackbar_error_no_internet)
            else -> getString(R.string.ws_error, HttpURLConnection.HTTP_UNAVAILABLE)
        }
        UMessage.showSnackBar(viewContent, this, message!!)
        // Restore UI
        this.loadingProgressBar.visibility = View.GONE
        this.btnLogin.isEnabled = true
        this.inputPassword.isEnabled = true
        this.inputUser.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewContent = findViewById(android.R.id.content)

        this.inputPassword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                if(actionId== BundleConstants.ACTION_ID || actionId == EditorInfo.IME_NULL){
                    return true
                }
                return false
            }
        })
    }

    override fun setLocal(location: Location?) {
        // get nearest local
        val local =
                LocalsRepository.getNearestLocal(AppSharedPreference.getInstance(this).sharedPreferences,
                location!!)
        if(local!=null){
            // set location icon
            tvLoginLocationIcon.setImageDrawable(ContextCompat.
                    getDrawable(this,R.drawable.ic_location_on)!!)
            // set message
            val type = if(local.type!=null) local.type else getString(R.string.empty)
            val name = if(local.name!=null) local.name else getString(R.string.empty)
            val city = if(local.city!=null) local.city else getString(R.string.empty)
            tvLoginLocation.text = getString(R.string.local_location, type, name, city)
        }else{
            // set location icon
            tvLoginLocationIcon.setImageDrawable(ContextCompat.
                    getDrawable(this,R.drawable.ic_location_off_grey_24dp)!!)
            // set message
            tvLoginLocation.text = getString(R.string.msg_no_local)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun doForgotPasswordOnClick(v: View) {

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.activity_login_forgot_pass, LinearLayout(this), false)
        val viewTitle = inflater.inflate(R.layout.activity_login_forgot_pass_title,
                                            LinearLayout(this), false)

        /**
         * ask security question
          */
        setSecurityQuestionView()

        /**
        ask for username
         */
        setCheckUsernameView()

        val builder = AlertDialog.Builder(this)
        builder.setCustomTitle(viewTitle)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setSecurityQuestionView() {
        val askSecurityQuestionLay = view.forgot_pass_question_lay
        val askSecurityQuestionText = view.forgot_pass_question_text
        val changeQuestion = view.forgot_password_dialog_question_change
        // answer edit text listener
        askSecurityQuestionLay.forgot_pass_question_answer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                view.forgot_pass_question_error.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        // change question listener
        changeQuestion.setOnClickListener {
            currentQuestion = getNextSecurityQuestion(currentQuestion)
            askSecurityQuestionText.text = currentQuestion.question
        }
        // submit listener
        view.forgot_pass_question_submit.setOnClickListener {
            val answer = askSecurityQuestionLay.forgot_pass_question_answer.text.toString()
            if(answer.isNotBlank()){
                // set views
                askSecurityQuestionLay.visibility = View.GONE
                view.forgot_pass_progressbar.visibility = View.VISIBLE
                hideKeyboard()
                // call ws
                val request = ForgotPassQuestionsRequest()
                request.userId = userData!!.userId
                request.questionId = currentQuestion.questionId
                request.answer = answer
                AccountServiceRepository.forgotPassSendSecurityQuestionAnswer(this, request, this)

            }else{
                view.forgot_pass_question_error.visibility = View.VISIBLE
            }
        }

    }

    private fun setCheckUsernameView(){
        val askUsernameLay = view.forgot_pass_username_lay
        val askUsernameSubmit = view.forgot_pass_username_submit
        // username edit text listener
        askUsernameLay.forgot_pass_username_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                view.forgot_pass_username_error.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        // submit listener
        askUsernameSubmit.setOnClickListener {
            val username = askUsernameLay.forgot_pass_username_et.text.toString()
            if(username.isNotBlank()){
                // set views
                askUsernameLay.visibility = View.GONE
                view.forgot_pass_progressbar.visibility = View.VISIBLE
                hideKeyboard()
                // call ws
                val request = ForgotPassUsernameRequest()
                request.userName = username
                AccountServiceRepository.forgotPassCheckUsername(this, request, this)
            }else{
                view.forgot_pass_username_error.visibility = View.VISIBLE
                view.forgot_pass_username_error_text.text =
                        getString(R.string.forgot_password_dialog_username_error)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun doLoginOnClick(v: View) {
        if (this.isValidForm()) {
            // Call login ws
            val request = LoginRequest()
            request.userName = this.inputUser.text.toString()
            request.password = this.inputPassword.text.toString()
            AccountServiceRepository.login(this, request, this)
        }
    }

    private fun isValidForm(): Boolean {
        return when {
            this.inputUser.text.isEmpty() -> {
                this.inputUser.error = getString(R.string.required_field)
                false
            }
            this.inputPassword.text.isEmpty() -> {
                this.inputPassword.error = getString(R.string.required_field)
                false
            }
            else -> true
        }
    }

    private fun onUsernameResponse(response: CheckUsernameResponse) {
        if( response.puedeTenerPreguntaSecreta!= null &&
                response.puedeTenerPreguntaSecreta!! &&
                response.colPregSecretas != null &&
                response.colPregSecretas!!.isNotEmpty()){
            // set obj
            userData = response
            var haveMail = true

            // set currentQuestion obj with the first question
            currentQuestion = response.colPregSecretas!!.first()

            // set username in view
            view.forgot_pass_question_lay.forgot_pass_question_username.text =
                    if(userData!!.userCompleteName==null) getString(R.string.no_data) else userData!!.userCompleteName
            // set email view
            view.forgot_pass_question_lay.forgot_pass_question_usermail.text =
                    if(userData!!.emailEmpresarial==null && userData!!.emailPersonal==null){
                        // no email to send data
                        haveMail = false
                        getString(R.string.no_data)
                    }else{
                        userData!!.emailEmpresarial
                    }

            // set question
            view.forgot_pass_question_text.text = currentQuestion.question
            // set views
            if(haveMail) {
                view.forgot_pass_username_lay.visibility = View.GONE
                view.forgot_pass_question_lay.visibility = View.VISIBLE
            }else{
                // no mail
                view.forgot_pass_username_lay.visibility = View.GONE
                view.forgot_pass_message.text =
                        getString(R.string.forgot_password_no_mail, userData!!.userCompleteName)
                view.forgot_pass_message_lay.visibility = View.VISIBLE
            }
        }else{
            // no security questions
            view.forgot_pass_username_lay.visibility = View.GONE
            view.forgot_pass_message.text = if(response.userCompleteName != null) {
                getString(R.string.forgot_password_no_questions, response.userCompleteName)
            }else{
                getString(R.string.forgot_password_no_questions)
            }
            view.forgot_pass_message_lay.visibility = View.VISIBLE
        }
    }

    private fun getNextSecurityQuestion(question: CheckUsernameResponse.QuestionsResponse):
                                        CheckUsernameResponse.QuestionsResponse {
        val nextIndex = userData!!.colPregSecretas!!.indexOf(question)+1
        currentQuestion = if(nextIndex == userData!!.colPregSecretas!!.size){
            userData!!.colPregSecretas!!.first()
        }else{
            userData!!.colPregSecretas!![nextIndex]
        }
        return currentQuestion
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        val delayTime = resources.getInteger(R.integer.two_k)
        this.doubleBackToExitPressedOnce = true
        UMessage.showSnackBar(viewContent, this, R.string.main_back_again)
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, delayTime.toLong())
    }
}
