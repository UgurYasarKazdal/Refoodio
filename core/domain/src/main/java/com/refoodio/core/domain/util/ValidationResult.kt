package com.refoodio.core.domain.util

data class ValidationResult(
    val successful: Boolean,
    val errorType: AppError? = null
)