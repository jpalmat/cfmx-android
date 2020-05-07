package ec.com.smx.cfmx.data.api.vo.request

import com.google.gson.annotations.SerializedName

/**
 * Created by Frederick on 21/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class GetCorpTokenRequest {
    @SerializedName("userId")
    var userId: String? = null
    @SerializedName("companyId")
    var companyId: String? = null
}
