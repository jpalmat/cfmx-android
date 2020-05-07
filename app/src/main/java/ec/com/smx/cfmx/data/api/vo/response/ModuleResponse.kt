package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class ModuleResponse : Serializable {
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("title")
    var title: String = ""
    @SerializedName("descripcion")
    var descripcion: String = ""
    @SerializedName("menuItems")
    var menuItems: List<OptionResponse> = ArrayList()
}
