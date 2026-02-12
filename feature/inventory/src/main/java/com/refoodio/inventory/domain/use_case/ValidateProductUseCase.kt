package com.refoodio.inventory.domain.use_case

import com.refoodio.core.util.UiText
import com.refoodio.inventory.R
import com.refoodio.inventory.domain.model.Product

// com/refoodio/inventory/domain/use_case/ValidateProductUseCase.kt
class ValidateProductUseCase {
    fun execute(product: Product): ValidationResult {
        if (product.name.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_product_name_cant_be_empty)
            )
        }
        if (product.quantity <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.error_quantity_must_be_greater_than_zero)            )
        }
        return ValidationResult(successful = true)
    }
}