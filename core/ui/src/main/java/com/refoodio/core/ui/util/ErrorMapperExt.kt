package com.refoodio.core.ui.util

import com.refoodio.core.domain.util.AppError
import com.refoodio.core.ui.R

fun AppError.handleWith(otherMapper: (AppError) -> UiText?): UiText {
    return this.asCommonUiText()
        ?: otherMapper(this)
        ?: UiText.StringResource(R.string.error_unknown)
}