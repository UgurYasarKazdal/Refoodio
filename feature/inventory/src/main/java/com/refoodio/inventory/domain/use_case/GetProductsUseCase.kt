package com.refoodio.inventory.domain.use_case

import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getAllProducts().map { list ->
            // İş Mantığı: Tarihi yakın olanı en üstte göster
            list.sortedBy { it.expiryDate }
        }
    }
}