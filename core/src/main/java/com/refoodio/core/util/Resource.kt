// core/src/main/java/com/refoodio/core/util/Resource.kt
package com.refoodio.core.util

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: UiText? = null, val exception: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}