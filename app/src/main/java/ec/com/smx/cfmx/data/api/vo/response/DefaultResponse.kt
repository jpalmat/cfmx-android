package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Frederick on 30/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class DefaultResponse  : Serializable {
    @SerializedName("response")
    var response: Response? = null
    class Response {
        @SerializedName("success")
        var success: Boolean? = null
        @SerializedName("status")
        var status: Boolean? = null
        @SerializedName("message")
        var message: String? = null
    }
}
