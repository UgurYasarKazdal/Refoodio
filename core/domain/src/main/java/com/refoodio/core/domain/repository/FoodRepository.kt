package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.catalog.FoodItem

interface FoodRepository {
    suspend fun getBarcodeInventory(barcode: String): Result<FoodItem>
}