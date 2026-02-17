package com.refoodio.core.domain.use_case.inventory.addInventory

import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.repository.FoodRepository

class GetFoodByBarcodeUseCase constructor(
    private val repository: FoodRepository
) {
    suspend operator fun invoke(barcode: String): Result<FoodItem> {
        // Burada gerekirse barkod formatı kontrolü gibi ek iş mantıkları eklenebilir
        if (barcode.isBlank()) {
            return Result.failure(Exception("Barkod boş olamaz"))
        }

        return repository.getProductByBarcode(barcode)
    }
}