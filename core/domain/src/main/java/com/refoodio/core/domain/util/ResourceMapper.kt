package com.refoodio.core.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import java.sql.SQLException

fun <T> Flow<T>.asResource(
    includeLoading: Boolean = true
): Flow<Resource<T, AppError>> {
    return this
        .map<T, Resource<T, AppError>> { Resource.Success(it) }
        .onStart { if (includeLoading) emit(Resource.Loading) }
        .catch { e ->
            val errorType = when (e) {
                is ValidationException -> e.errorType
                is SQLException -> CommonError.DATABASE_ERROR
                is IOException -> CommonError.NETWORK_ERROR
                else -> CommonError.UNKNOWN_ERROR
            }
            emit(Resource.Error(errorType = errorType, message = e.message))
        }
}