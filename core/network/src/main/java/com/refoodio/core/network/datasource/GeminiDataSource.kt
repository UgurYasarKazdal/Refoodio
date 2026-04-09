package com.refoodio.core.network.datasource

import com.refoodio.core.network.api.GeminiApiService
import com.refoodio.core.network.dto.recipe.GeminiRequest
import com.refoodio.core.network.dto.recipe.GeminiResponse
import com.refoodio.core.network.exception.ApiException
import retrofit2.HttpException
import javax.inject.Inject

/**
 * GeminiApiService'i saran katman.
 * Retrofit'in HttpException'ını ApiException'a dönüştürür,
 * böylece core:data hiç retrofit2.* import'u taşımaz.
 */
class GeminiDataSource @Inject constructor(
    private val service: GeminiApiService
) {
    suspend fun generateContent(
        model: String,
        apiKey: String,
        request: GeminiRequest
    ): GeminiResponse {
        return try {
            service.generateContent(model, apiKey, request)
        } catch (e: HttpException) {
            throw ApiException(e.code(), e.message())
        }
    }
}
