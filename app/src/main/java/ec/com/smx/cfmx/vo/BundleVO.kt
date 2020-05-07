package ec.com.smx.cfmx.vo

import com.google.gson.annotations.SerializedName

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class BundleVO {
    @SerializedName("action")
    var action: String? = null
    @SerializedName("user")
    var user: UserVO? = null
    @SerializedName("viewCode")
    var viewCode: String? = null
    @SerializedName("controllerServerAddress")
    var controllerServerAddress: String? = null
    @SerializedName("controllerServerPort")
    var controllerServerPort: Int? = null
    @SerializedName("localCode")
    var localCode: Int? = null
    @SerializedName("localRefCode")
    var localRefCode: Int? = null
    @SerializedName("localName")
    var localName: String? = null
    @SerializedName("corpToken")
    var corpToken: String? = null
}
