package ec.com.smx.cfmx.data.api.vo.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Frederick on 21/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class GetCorpTokenResponse {
    @SerializedName("token")
    var token: String? = null
}
