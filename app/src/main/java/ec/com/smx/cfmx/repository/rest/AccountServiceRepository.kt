package ec.com.smx.cfmx.repository.rest

import android.content.Context
import ec.com.smx.cfmx.data.api.AccountServiceCall
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
import ec.com.smx.cfmx.data.persistence.entity.Option

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class AccountServiceRepository private constructor() {
    companion object {
        // check valid session token
        fun checkToken(context: Context, token: String, listener: CheckTokenServiceListener) {
            val service = AccountServiceCall(context)
            return service.checkToken(token, listener)
        }
        // forgot password
        fun changePass(context: Context, token: String,
                       request: ChangePasswordRequest,
                       listener: ChangePasswordServiceListener) {
            val service = AccountServiceCall(context)
            return service.changePass(token, request, listener)
        }

        fun login(context: Context, request: LoginRequest, listener: LoginServiceListener) {
            val service = AccountServiceCall(context)
            return service.login(request, listener)
        }

        fun logout(context: Context, token: String) {
            val service = AccountServiceCall(context)
            service.logout(token)
        }
        // forgot password first step: check username
        fun forgotPassCheckUsername(context: Context, request: ForgotPassUsernameRequest,
                                    listener: ForgotPasswordListener) {
            val service = AccountServiceCall(context)
            service.checkUsername(request, listener, 0)
        }

        fun forgotPassSendSecurityQuestionAnswer(context: Context, request: ForgotPassQuestionsRequest,
                                                 listener: ForgotPasswordListener) {
            val service = AccountServiceCall(context)
            service.sendAnswer(request, listener, 1)
        }

        fun getCorpToken(context: Context, request: GetCorpTokenRequest, token: String,
                         listener: GetCorpTokenListener, option: Option){
            val service = AccountServiceCall(context)
            service.getCorpToken(request, token, listener, option)
        }
    }
}
