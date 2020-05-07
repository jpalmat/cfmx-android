package ec.com.smx.cfmx.data.api.listener

import ec.com.smx.cfmx.data.api.vo.response.GetCorpTokenResponse
import ec.com.smx.cfmx.data.persistence.entity.Option

/**
 * Created by Frederick on 21/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
interface GetCorpTokenListener {
    fun onGetCorpTokenResponse(response: GetCorpTokenResponse?, option: Option)
    fun onGetCorpTokenFailure(t: Throwable)
}
