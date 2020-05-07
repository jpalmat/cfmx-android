package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Frederick on 19/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ChangePasswordResponse {
    @SerializedName("message")
    var message: String? = null
    @SerializedName("status")
    var status: Boolean? = null
}
