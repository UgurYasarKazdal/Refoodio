package com.refoodio.core.domain.use_case.inventory

import com.refoodio.core.domain.util.AppError

enum class InventoryErrors : AppError {
    EMPTY_NAME, INVALID_QUANTITY
}