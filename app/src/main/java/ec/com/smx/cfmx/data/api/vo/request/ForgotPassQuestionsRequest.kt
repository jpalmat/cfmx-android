package ec.com.smx.cfmx.data.api.vo.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Frederick on 14/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ForgotPassQuestionsRequest {
    @SerializedName("userId")
    var userId: String? = null
    @SerializedName("questionId")
    var questionId: Long? = null
    @SerializedName("answer")
    var answer: String? = null
}
