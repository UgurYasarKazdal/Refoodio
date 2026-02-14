package com.refoodio.core.ui.util

import android.content.Context
import androidx.annotation.StringRes

/**
 * UI katmanında metinleri yönetmek için kullanılan bir sınıf.
 * Bu sınıf, metnin dinamik bir String mi yoksa bir String kaynağı mı olduğunu belirtir.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    data class StringResource(@StringRes val resId: Int) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId)
        }
    }
}