package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Frederick on 01/08/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class LocalResponse : Serializable {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("type")
    var type: String? = null
    @SerializedName("city")
    var city: String? = null
    @SerializedName("referenceCode")
    var referenceCode: Int? = null
    @SerializedName("latitude")
    var latitude: Double? = null
    @SerializedName("longitude")
    var longitude: Double? = null
    @SerializedName("ipLocal")
    var ipLocal: String? = null
    @SerializedName("portLocal")
    var portLocal: Int? = null
}
