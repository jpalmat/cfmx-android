package ec.com.smx.cfmx.data.api.exception

/**
 * Created by Frederick on 18/09/2018.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class CustomException  : Exception {

    constructor() : super()

    constructor(detailMessage: String?) : super(detailMessage)

    constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable)
}
