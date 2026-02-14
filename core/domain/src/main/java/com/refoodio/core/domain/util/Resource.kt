package com.refoodio.core.domain.util

// core:domain/util/Resource.kt
// core:domain/util/Resource.kt
sealed class Resource<out T, out E : AppError> {
    data class Success<out T>(val data: T) : Resource<T, Nothing>()

    data class Error<out E : AppError>(
        val errorType: E,           // Artık tipi belli
        val message: String? = null,
        val exception: Throwable? = null
    ) : Resource<Nothing, E>()

    data object Loading : Resource<Nothing, Nothing>()
}