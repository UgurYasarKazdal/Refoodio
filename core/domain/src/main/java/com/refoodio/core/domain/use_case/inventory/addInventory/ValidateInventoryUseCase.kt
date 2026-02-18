package com.refoodio.core.domain.use_case.inventory.addInventory

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.use_case.inventory.InventoryErrors
import com.refoodio.core.domain.util.ValidationResult

class ValidateInventoryUseCase {
    fun execute(product: InventoryItem): ValidationResult {
        if (product.name.isBlank()) {
            return ValidationResult(
                successful = false,
                errorType = InventoryErrors.EMPTY_NAME
            )
        }
        if (product.quantity <= 0) {
            return ValidationResult(
                successful = false,
                errorType = InventoryErrors.INVALID_QUANTITY
            )
        }
        return ValidationResult(successful = true)
    }
}