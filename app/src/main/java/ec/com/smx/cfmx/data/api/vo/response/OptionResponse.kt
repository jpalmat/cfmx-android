package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class OptionResponse : Serializable {
    @SerializedName("id")
    var id: Long = 0
    @SerializedName("title")
    var title: String = ""
    @SerializedName("descripcion")
    var descripcion: String? = null
    @SerializedName("href")
    var href: String = ""
    @SerializedName("estiloPanel")
    var estiloPanel: String? = null
    @SerializedName("ordenMenu")
    var ordenMenu: Int = 0
    @SerializedName("favorito")
    var favorito: Boolean = false
    @SerializedName("codigoVentana")
    var codigoVentana: String? = null
}
