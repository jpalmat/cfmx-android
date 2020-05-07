package ec.com.smx.cfmx.data.api.listener

import ec.com.smx.cfmx.data.api.vo.response.CheckUsernameResponse
import ec.com.smx.cfmx.data.api.vo.response.ForgotPassQuestionsResponse

/**
 * Created by Frederick on 14/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface ForgotPasswordListener {
    fun onCheckUsernameResponse(response: CheckUsernameResponse?)
    fun onSendSecurityQuestionAnswerResponse(response: ForgotPassQuestionsResponse?)
    fun onForgotPasswordFailure(t: Throwable, type: Int)
}
