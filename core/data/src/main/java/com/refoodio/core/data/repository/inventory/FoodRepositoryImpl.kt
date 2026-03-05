package com.refoodio.core.data.repository.inventory

import com.refoodio.core.data.mapper.inventory.toDomainModel
import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.repository.FoodRepository
import com.refoodio.core.network.api.FoodApi
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val api: FoodApi
) : FoodRepository {

    override suspend fun getBarcodeInventory(barcode: String): Result<FoodItem> {
        return try {
            val response = api.getInventoryByBarcode(barcode)
            val inventoryDto = response.barcodeInventory
            if (response.status == 1 && inventoryDto != null) {
                val domainModel = inventoryDto.toDomainModel()
                Result.success(domainModel)
            } else {
                Result.failure(Exception("Ürün bulunamadı (Status: ${response.status})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}