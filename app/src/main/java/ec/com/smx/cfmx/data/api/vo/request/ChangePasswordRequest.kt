package ec.com.smx.cfmx.data.api.vo.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Frederick on 19/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ChangePasswordRequest {
    @SerializedName("userId")
    var userId: String? = null
    @SerializedName("oldPassword")
    var oldPassword: String? = null
    @SerializedName("newPassword")
    var newPassword: String? = null
}
