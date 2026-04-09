package com.refoodio.core.network.interceptor

import com.refoodio.core.network.exception.ApiException
import okhttp3.Interceptor
import okhttp3.Response

/**
 * HTTP hata kodlarını (4xx / 5xx) Retrofit bağımlılığı gerektirmeyen
 * ApiException'a dönüştürür. Böylece core:data katmanı Retrofit'e
 * doğrudan bağımlı olmak zorunda kalmaz.
 */
class HttpErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            val code = response.code
            val message = response.body?.string() ?: "HTTP $code"
            response.close()
            throw ApiException(code, message)
        }
        return response
    }
}
