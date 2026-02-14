package com.refoodio.inventory.presentation.util
// feature:inventory/src/main/java/.../presentation/util/InventoryErrorMapper.kt

import com.refoodio.core.domain.use_case.inventory.InventoryErrors
import com.refoodio.core.domain.util.AppError
import com.refoodio.core.ui.util.UiText
import com.refoodio.core.ui.util.handleWith
import com.refoodio.inventory.R

fun AppError.asInventoryErrorText(): UiText { // İsmi sadeleştirdik: asUiText()
    return this.handleWith { error ->
        when (error) {
            is InventoryErrors -> when (error) {
                // Burada artık is is is yazmıyorsun, sadece dalları yazıyorsun
                InventoryErrors.EMPTY_NAME -> UiText.StringResource(R.string.error_empty_name)
                InventoryErrors.INVALID_QUANTITY -> UiText.StringResource(R.string.error_invalid_quantity)
            }
            else -> null
        }
    }
}