package ec.com.smx.cfmx.vo

import com.google.gson.annotations.SerializedName

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class OptionDataVO {
    @SerializedName("color")
    var color: String = ""
    @SerializedName("android")
    var android: String = ""
    @SerializedName("iOS")
    var iOS: String = ""
}
