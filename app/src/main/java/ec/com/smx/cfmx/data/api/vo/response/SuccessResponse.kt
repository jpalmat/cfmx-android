package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class SuccessResponse : Serializable {
    @SerializedName("code")
    var code: Int? = null
    @SerializedName("message")
    var message: String? = null
}
