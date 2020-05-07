package ec.com.smx.cfmx.data.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ec.com.smx.cfmx.R
import ec.com.smx.cfmx.data.api.exception.CustomException
import ec.com.smx.cfmx.data.api.helper.RetrofitHelper
import ec.com.smx.cfmx.data.api.listener.ChangePasswordServiceListener
import ec.com.smx.cfmx.data.api.listener.CheckTokenServiceListener
import ec.com.smx.cfmx.data.api.listener.ForgotPasswordListener
import ec.com.smx.cfmx.data.api.listener.GetCorpTokenListener
import ec.com.smx.cfmx.data.api.listener.LoginServiceListener
import ec.com.smx.cfmx.data.api.vo.request.ChangePasswordRequest
import ec.com.smx.cfmx.data.api.vo.request.ForgotPassQuestionsRequest
import ec.com.smx.cfmx.data.api.vo.request.ForgotPassUsernameRequest
import ec.com.smx.cfmx.data.api.vo.request.GetCorpTokenRequest
import ec.com.smx.cfmx.data.api.vo.request.LoginRequest
import ec.com.smx.cfmx.data.api.vo.response.ChangePasswordResponse
import ec.com.smx.cfmx.data.api.vo.response.CheckUsernameResponse
import ec.com.smx.cfmx.data.api.vo.response.DefaultResponse
import ec.com.smx.cfmx.data.api.vo.response.ForgotPassQuestionsResponse
import ec.com.smx.cfmx.data.api.vo.response.GetCorpTokenResponse
import ec.com.smx.cfmx.data.api.vo.response.SuccessResponse
import ec.com.smx.cfmx.data.persistence.entity.Option
import ec.com.smx.cfmx.data.persistence.entity.User
import ec.com.smx.kcommons.util.UNetwork
import ec.com.smx.kcommonsencrypt.UEncryption
import org.json.JSONObject
import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit
import java.net.ConnectException
import java.net.CookieManager
import java.net.HttpURLConnection

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class AccountServiceCall(context: Context) : ServiceCall(context) {

    private fun getCookieValue(retrofitHelper: RetrofitHelper): String? {
        var cookieValue = ""
        val cookieList = retrofitHelper.cookieManager.cookieStore.cookies
        cookieList?.filter { "JSESSIONID".equals(it.name, ignoreCase = true) }?.forEach { cookieValue = it.value }
        return cookieValue
    }

    // check token web service
    fun checkToken(token: String, listener: CheckTokenServiceListener) {
        val client = this.getClientWithHeader(this.url, token)
        val service = client.create(AccountService::class.java)
        val call = service.checkToken()
        // check available connection
        if(UNetwork.isConnected(context)){
            call.enqueue(object : Callback<SuccessResponse> {
                override fun onResponse(response: Response<SuccessResponse>, retrofit: Retrofit) {
                    when (response.code()) {
                        HttpURLConnection.HTTP_OK -> listener.onCheckTokenResponse()
                        HttpURLConnection.HTTP_UNAUTHORIZED -> onFailure(CustomException())
                        else -> onFailure(Exception())
                    }
                }
                override fun onFailure(t: Throwable) {
                    listener.onCheckTokenFailure(t)
                }
            })
        }else{
            listener.onCheckTokenFailure(Exception())
        }
    }

    // change password web service
    fun changePass(token: String, request: ChangePasswordRequest, listener: ChangePasswordServiceListener) {
        val client = this.getClientWithHeader(this.url, token)
        val service = client.create(AccountService::class.java)
        val call = service.changePass(request)
        // check available connection
        if(UNetwork.isConnected(context)){
            callChangePass(call, listener)
        }else{
            // send offline error
            listener.onChangePassFailure(ConnectException(context.getString(R.string.snackbar_error_no_internet)))
        }
    }

    private fun callChangePass(call: Call<ChangePasswordResponse>,
                               listener: ChangePasswordServiceListener) {
        call.enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(response: Response<ChangePasswordResponse>, retrofit: Retrofit?) {
                // check response
                when (response.code()) {
                    HttpURLConnection.HTTP_OK ->
                        listener.onChangePassResponse(response.body())
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        onFailure(CustomException(context.getString(R.string.ws_change_pass_error)))
                    else -> {
                        // parse error response
                        val json = response.errorBody().string()
                        try {
                            val obj = Gson().fromJson(json, ChangePasswordResponse::class.java)
                            if(obj.message!=null && obj.status != null){
                                this.onFailure(CustomException(obj!!.message))
                            }else{
                                onFailure(CustomException())
                            }
                        }catch(e: JsonSyntaxException) {
                            onFailure(CustomException())
                        }
                    }
            }
            }
            override fun onFailure(t: Throwable) {
                // fail listener response
                listener.onChangePassFailure(t)
            }
        })

    }

    // login web service
    fun login(request: LoginRequest, listener: LoginServiceListener) {
        val retrofitHelper = RetrofitHelper(CookieManager(), this.context)
        listener.onLoginPreExecute()
        val httpClient = retrofitHelper.getOkHttpClient()
        val client = retrofitHelper.getRetrofitBase(this.url, httpClient)
        val call = client.create(AccountService::class.java).login(request)
        // check available connection
        if(UNetwork.isConnected(context)){
            callLogin(request, call, retrofitHelper, listener)
        }else{
            // send offline error
            listener.onLoginFailure(ConnectException())
        }
    }

    private fun callLogin(request: LoginRequest,
                          call: Call<User>,
                          retrofitHelper: RetrofitHelper,
                          listener: LoginServiceListener) {
        call.enqueue(object : Callback<User> {
            override fun onResponse(response: Response<User>, retrofit: Retrofit?) {
                // check response
                when (response.code()) {
                    HttpURLConnection.HTTP_OK -> {
                        val res: User = response.body()
                        val token = getCookieValue(retrofitHelper)
                        res.password = UEncryption.encrypt(request.password!!)
                        res.token = token
                        listener.onLoginResponse(res)
                    }
                    HttpURLConnection.HTTP_GATEWAY_TIMEOUT ->
                        this.onFailure(CustomException(context.getString(R.string.ws_error,
                                HttpURLConnection.HTTP_GATEWAY_TIMEOUT)))
                    HttpURLConnection.HTTP_CLIENT_TIMEOUT ->
                        this.onFailure(CustomException(context.getString(R.string.ws_error,
                                HttpURLConnection.HTTP_CLIENT_TIMEOUT)))
                    HttpURLConnection.HTTP_INTERNAL_ERROR ->
                        this.onFailure(CustomException(context.getString(R.string.ws_error,
                                HttpURLConnection.HTTP_INTERNAL_ERROR)))
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        this.onFailure(CustomException(context.getString(R.string.ws_error,
                                HttpURLConnection.HTTP_BAD_REQUEST)))
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        this.onFailure(parseJson(response))
                    }
                    else -> onFailure(CustomException(context.getString(R.string.ws_error,
                            HttpURLConnection.HTTP_CONFLICT)))
                }
            }
            override fun onFailure(t: Throwable) {
                // fail listener response
                listener.onLoginFailure(t)
            }
        })
    }

    private fun parseJson(response: Response<User>): CustomException {
        val json = response.errorBody().string()
        return try {
            val obj = Gson().fromJson(json, User::class.java)
            if(obj.response!=null && obj.response!!.message != null){
                CustomException(obj.response!!.message)
            }else{
                CustomException(context.getString(R.string.ws_error,
                        HttpURLConnection.HTTP_CONFLICT))
            }
        }catch(e: JsonSyntaxException){
            CustomException(context.getString(R.string.ws_error,
                    HttpURLConnection.HTTP_CONFLICT))
        }
    }

    // logout web service
    fun logout(token: String) {
        val client = this.getClientWithHeader(this.url, token)
        val service = client.create(AccountService::class.java)
        val call = service.logout(JSONObject())
        // check available connection
        if(UNetwork.isConnected(context)){
            call.enqueue(object : Callback<SuccessResponse> {
                // do nothing
                override fun onResponse(response: Response<SuccessResponse>, retrofit: Retrofit) {}
                // do nothing
                override fun onFailure(t: Throwable) {}
            })
        }else{
            // Do nothing
        }
    }

    // check valid username for forgot password web service
    fun checkUsername(request: ForgotPassUsernameRequest,
                      listener: ForgotPasswordListener,
                      type: Int) {
        val retrofitHelper = RetrofitHelper(CookieManager(), this.context)
        val httpClient = retrofitHelper.getOkHttpClient()
        val client = retrofitHelper.getRetrofitBase(this.url, httpClient)
        val call = client.create(AccountService::class.java).checkUsername(request)
        // check available connection
        if(UNetwork.isConnected(context)){
            callCheckUsername(call, listener, type)
        }else{
            // send offline error
            listener.onForgotPasswordFailure(ConnectException(), type)
        }
    }

    // checkUsername service call
    private fun callCheckUsername(call: Call<CheckUsernameResponse>,
                                  listener: ForgotPasswordListener,
                                  type: Int) {
        call.enqueue(object : Callback<CheckUsernameResponse> {
            override fun onResponse(response: Response<CheckUsernameResponse>, retrofit: Retrofit?) {
                when (response.code()) {
                    HttpURLConnection.HTTP_OK -> listener.onCheckUsernameResponse(response.body())
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val json = response.errorBody().string()
                        try {
                            val obj = Gson().fromJson(json, DefaultResponse::class.java)
                            if(obj.response!=null && obj.response!!.message != null){
                                this.onFailure(CustomException(obj.response!!.message))
                            }else{
                                this.onFailure(CustomException(context.getString(R.string.ws_error,
                                        HttpURLConnection.HTTP_CONFLICT)))
                            }
                        }catch(e: JsonSyntaxException){
                            this.onFailure(CustomException(context.getString(R.string.ws_error,
                                    HttpURLConnection.HTTP_CONFLICT)))
                        }
                    }
                    else -> onFailure(CustomException(response.code().toString()))
                }
            }

            override fun onFailure(t: Throwable) {
                listener.onForgotPasswordFailure(t, type)
            }
        })
    }

    // send security question answer web service
    fun sendAnswer(request: ForgotPassQuestionsRequest,
                   listener: ForgotPasswordListener,
                   type: Int) {
        val retrofitHelper = RetrofitHelper(CookieManager(), this.context)
        val httpClient = retrofitHelper.getOkHttpClient()
        val client = retrofitHelper.getRetrofitBase(this.url, httpClient)
        val call = client.create(AccountService::class.java).sendAnswer(request)
        // check available connection
        if(UNetwork.isConnected(context)){
            callSendAnswer(call, listener, type)
        }else{
            listener.onForgotPasswordFailure(ConnectException(), type)
        }
    }
    // send answer service call
    private fun callSendAnswer(call: Call<ForgotPassQuestionsResponse>,
                               listener: ForgotPasswordListener,
                               type: Int) {
        call.enqueue(object : Callback<ForgotPassQuestionsResponse> {
            override fun onResponse(response: Response<ForgotPassQuestionsResponse>, retrofit: Retrofit?) {
                when (response.code()) {
                    HttpURLConnection.HTTP_OK -> listener.onSendSecurityQuestionAnswerResponse(response.body())
                    HttpURLConnection.HTTP_BAD_REQUEST -> {
                        val json = response.errorBody().string()
                        try {
                            val obj = Gson().fromJson(json, DefaultResponse.Response::class.java)
                            if(obj?.message != null){
                                this.onFailure(CustomException(obj.message))
                            }else{
                                this.onFailure(CustomException(context.getString(R.string.ws_error,
                                        HttpURLConnection.HTTP_CONFLICT)))
                            }
                        }catch(e: JsonSyntaxException){
                            this.onFailure(CustomException(context.getString(R.string.ws_error,
                                    HttpURLConnection.HTTP_CONFLICT)))
                        }
                    }
                    else -> onFailure(CustomException(response.code().toString()))
                }
            }
            override fun onFailure(t: Throwable) {
                listener.onForgotPasswordFailure(t, type)
            }
        })
    }

    // get corp token web service
    fun getCorpToken(request: GetCorpTokenRequest, token: String,
                     listener: GetCorpTokenListener, option: Option) {
        val client = this.getClientWithHeader(this.url, token)
        val service = client.create(AccountService::class.java)
        val call = service.getCorpToken(request)
        // check available connection
        if(UNetwork.isConnected(context)){
            callGetCorpToken(call, listener, option)
        }else{
            listener.onGetCorpTokenFailure(ConnectException(""))
        }
    }
    // get corp token service call
    private fun callGetCorpToken(call: Call<GetCorpTokenResponse>,
                                 listener: GetCorpTokenListener,
                                 option: Option) {
        call.enqueue(object : Callback<GetCorpTokenResponse> {
            override fun onResponse(response: Response<GetCorpTokenResponse>, retrofit: Retrofit?) {
                when (response.code()) {
                    HttpURLConnection.HTTP_OK -> listener.onGetCorpTokenResponse(response.body(), option)
                    else -> onFailure(CustomException(response.code().toString()))
                }
            }
            override fun onFailure(t: Throwable) {
                listener.onGetCorpTokenFailure(t)
            }
        })
    }
}
