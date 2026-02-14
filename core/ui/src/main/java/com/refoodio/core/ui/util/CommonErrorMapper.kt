package com.refoodio.core.ui.util

import com.refoodio.core.domain.util.AppError
import com.refoodio.core.domain.util.CommonError
import com.refoodio.core.ui.R

fun AppError.asCommonUiText(): UiText? {
    return when (this) {
        is CommonError -> {
            when (this) {
                CommonError.DATABASE_ERROR -> UiText.StringResource(R.string.error_db)
                CommonError.NETWORK_ERROR -> UiText.StringResource(R.string.error_network)
                CommonError.UNKNOWN_ERROR -> UiText.StringResource(R.string.error_unknown)
            }
        }
        else -> null // Eğer genel bir hata değilse null dön ki feature modülü devralabilsin
    }
}