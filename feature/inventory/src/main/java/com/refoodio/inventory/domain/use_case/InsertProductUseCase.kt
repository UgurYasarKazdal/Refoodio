package com.refoodio.inventory.domain.use_case

import com.refoodio.core.util.Resource
import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertProductUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    operator fun invoke(product: Product): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading) // Yarışı "Yükleniyor" ile başlat

            if (product.name.isBlank()) {
                emit(Resource.Error("Ürün adı boş olamaz!"))
                return@flow
            }

            if (product.quantity <= 0) {
                emit(Resource.Error(message = "Miktar 0'dan büyük olmalı!"))
                return@flow
            }

            repository.addProduct(product)
            emit(Resource.Success(Unit)) // Yarış başarıyla bitti
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Bir hata oluştu"))
        }
    }
}