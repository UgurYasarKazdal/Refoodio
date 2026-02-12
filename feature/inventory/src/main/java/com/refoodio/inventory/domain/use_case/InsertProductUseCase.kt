package com.refoodio.inventory.domain.use_case

import com.refoodio.core.util.Resource
import com.refoodio.core.util.UiText
import com.refoodio.inventory.R
import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertProductUseCase @Inject constructor(
    private val repository: InventoryRepository,
    private val validateProduct: ValidateProductUseCase // Bağımlılık olarak ekleyin
) {
    operator fun invoke(product: Product): Flow<Resource<Unit>> = flow {
        try {
            val validationResult = validateProduct.execute(product)
            if (!validationResult.successful) {
                emit(
                    Resource.Error(
                        validationResult.errorMessage
                            ?: UiText.StringResource(R.string.error_invalid_product)
                    )
                )
                return@flow
            }

            emit(Resource.Loading) // Yarışı "Yükleniyor" ile başlat

            repository.addProduct(product)
            emit(Resource.Success(Unit)) // Yarış başarıyla bitti
        } catch (e: Exception) {
            // Dinamik bir mesaj için DynamicString veya genel bir hata için StringResource kullanabilirsiniz.
            emit(
                Resource.Error(
                // e.message boş değilse onu göster, boşsa genel bir hata mesajı göster.
                message = e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.error_unknown)
            ))
        }
    }
}