package com.refoodio.inventory.domain.use_case

import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.repository.InventoryRepository
import javax.inject.Inject

class InsertProductUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(product: Product) {
        // İş mantığı: Negatif miktar girilmesini burada engelleyebiliriz
        if (product.quantity > 0) {
            repository.addProduct(product)
        }
    }
}