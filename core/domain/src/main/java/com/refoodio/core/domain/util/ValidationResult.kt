package com.refoodio.core.domain.util

import com.refoodio.core.domain.util.AppError

data class ValidationResult(
    val successful: Boolean,
    val errorType: AppError? = null
)