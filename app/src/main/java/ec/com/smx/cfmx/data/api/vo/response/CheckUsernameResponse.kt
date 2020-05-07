package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Frederick on 14/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class CheckUsernameResponse {
    @SerializedName("userId")
    var userId: String? = null
    @SerializedName("userName")
    var userName: String? = null
    @SerializedName("userCompleteName")
    var userCompleteName: String? = null
    @SerializedName("tipoUsuario")
    var tipoUsuario: String? = null
    @SerializedName("systemId")
    var systemId: String? = null
    @SerializedName("emailEmpresarial")
    var emailEmpresarial: String? = null
    @SerializedName("emailPersonal")
    var emailPersonal: String? = null
    @SerializedName("puedeTenerPreguntaSecreta")
    var puedeTenerPreguntaSecreta: Boolean? = null
    @SerializedName("colPregSecretas")
    var colPregSecretas: List<QuestionsResponse>? = null

    class QuestionsResponse {
        @SerializedName("systemId")
        var systemId: String? = null
        @SerializedName("questionId")
        var questionId: Long? = null
        @SerializedName("answer")
        var answer: String? = null
        @SerializedName("question")
        var question: String? = null
    }
}
