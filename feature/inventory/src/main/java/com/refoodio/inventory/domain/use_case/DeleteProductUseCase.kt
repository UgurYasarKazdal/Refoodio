package com.refoodio.inventory.domain.use_case

import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.repository.InventoryRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.deleteProduct(product)
    }
}