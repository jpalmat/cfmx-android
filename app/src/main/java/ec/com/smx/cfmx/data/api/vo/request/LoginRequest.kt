package ec.com.smx.cfmx.data.api.vo.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LoginRequest {
    @SerializedName("userName")
    var userName: String? = null
    @SerializedName("password")
    var password: String? = null
}

