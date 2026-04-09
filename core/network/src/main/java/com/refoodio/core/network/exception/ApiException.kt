package com.refoodio.core.network.exception

import java.io.IOException

class ApiException(val code: Int, message: String) : IOException(message) {
    val isRetryable: Boolean get() = code in listOf(429, 503)
    val isFatal: Boolean get() = code in listOf(400, 401, 403, 404)
}
