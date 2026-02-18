package com.refoodio.core.data.repository.inventory

import com.refoodio.core.data.mapper.inventory.toDomainModel
import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.repository.FoodRepository
import com.refoodio.core.network.api.FoodApi
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val api: FoodApi
) : FoodRepository {

    override suspend fun getProductByBarcode(barcode: String): Result<FoodItem> {
        return try {
            val response = api.getProductByBarcode(barcode)
            val productDto = response.product
            if (response.status == 1 && productDto != null) {
                val domainModel = productDto.toDomainModel()
                Result.success(domainModel)
            } else {
                Result.failure(Exception("Ürün bulunamadı (Status: ${response.status})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}