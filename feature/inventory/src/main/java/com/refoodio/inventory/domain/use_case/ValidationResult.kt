package com.refoodio.inventory.domain.use_case

import com.refoodio.core.util.UiText

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: UiText? = null // String'den UiText'e değiştirildi
)