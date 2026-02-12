package com.refoodio.inventory.domain.use_case

import com.refoodio.core.util.Resource
import com.refoodio.core.util.UiText
import com.refoodio.inventory.R
import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    /**
     * Tüm ürünleri alır, son kullanma tarihine göre sıralar ve durumu Resource ile sarmalayarak yayınlar.
     * Bu, veritabanındaki her değişiklikte otomatik olarak yeni bir liste yayınlayan reaktif bir akıştır.
     */
    operator fun invoke(): Flow<Resource<List<Product>>> {
        return repository.getAllProducts() // 1. Orijinal Flow'u al
            .map<List<Product>, Resource<List<Product>>> { products ->
                // 2. Başarılı veri geldiğinde, sırala ve Resource.Success ile sarmala
                Resource.Success(products.sortedBy { it.expiryDate })
            }
            .onStart {
                // 3. Flow akmaya başlamadan hemen önce Loading durumunu yayınla
                emit(Resource.Loading)
            }
            .catch { exception ->
                // 4. Flow sırasında bir hata olursa, yakala ve Resource.Error yayınla
                emit(
                    Resource.Error(
                        message = exception.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_unknown)
                ))
            }
    }
}