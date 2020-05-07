package ec.com.smx.cfmx.data.api.vo.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Frederick on 14/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ForgotPassUsernameRequest {
    @SerializedName("userName")
    var userName: String = ""
}
