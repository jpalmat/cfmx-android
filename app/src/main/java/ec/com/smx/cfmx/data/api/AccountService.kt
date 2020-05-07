package ec.com.smx.cfmx.data.api

import ec.com.smx.cfmx.data.api.vo.request.ChangePasswordRequest
import ec.com.smx.cfmx.data.api.vo.request.ForgotPassQuestionsRequest
import ec.com.smx.cfmx.data.api.vo.request.ForgotPassUsernameRequest
import ec.com.smx.cfmx.data.api.vo.request.GetCorpTokenRequest
import ec.com.smx.cfmx.data.api.vo.request.LoginRequest
import ec.com.smx.cfmx.data.api.vo.response.ChangePasswordResponse
import ec.com.smx.cfmx.data.api.vo.response.CheckUsernameResponse
import ec.com.smx.cfmx.data.api.vo.response.ForgotPassQuestionsResponse
import ec.com.smx.cfmx.data.api.vo.response.GetCorpTokenResponse
import ec.com.smx.cfmx.data.api.vo.response.SuccessResponse
import ec.com.smx.cfmx.data.persistence.entity.User
import org.json.JSONObject
import retrofit.Call
import retrofit.http.Body
import retrofit.http.GET
import retrofit.http.Headers
import retrofit.http.POST

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface AccountService {

    // change password web service
    @Headers("Accept: application/json")
    @POST("/authProvider/auth/changePassword")
    fun changePass(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

    // check token  web service
    @Headers("Accept: application/json")
    @GET("/authProvider/auth/checkToken")
    fun checkToken(): Call<SuccessResponse>

    // login web service
    @Headers("Accept: application/json")
    @POST("/authProvider/auth/login")
    fun login(@Body request: LoginRequest): Call<User>

    // logout web service
    @Headers("Accept: application/json")
    @POST("/authProvider/auth/logout")
    fun logout(@Body emptyJson: JSONObject): Call<SuccessResponse>

    // check valid username for forgot password web service
    @Headers("Accept: application/json")
    @POST("/authProvider/auth/forgotPassword")
    fun checkUsername(@Body request: ForgotPassUsernameRequest): Call<CheckUsernameResponse>

    // send security question answer web service
    @Headers("Accept: application/json")
    @POST("/authProvider/auth/validateUserAnswer")
    fun sendAnswer(@Body request: ForgotPassQuestionsRequest): Call<ForgotPassQuestionsResponse>

    // get corp token web service
    @Headers("Accept: application/json")
    @POST("/authProvider/auth/getCorpToken")
    fun getCorpToken(@Body request: GetCorpTokenRequest): Call<GetCorpTokenResponse>
}
