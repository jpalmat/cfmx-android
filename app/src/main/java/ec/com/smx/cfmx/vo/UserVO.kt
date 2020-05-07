package ec.com.smx.cfmx.vo

import com.google.gson.annotations.SerializedName

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class UserVO {
    @SerializedName("code")
    var code: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("profileId")
    var profileId: String? = null
    @SerializedName("companyId")
    var companyId: String? = null
    @SerializedName("functionalities")
    var functionalities: List<String>? = null
}
