package com.refoodio.core.domain.use_case.inventory

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.util.ValidationResult

// com/refoodio/inventory/domain/use_case/ValidateProductUseCase.kt
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