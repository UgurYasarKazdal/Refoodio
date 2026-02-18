package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.catalog.FoodItem

interface FoodRepository {
    suspend fun getProductByBarcode(barcode: String): Result<FoodItem>
}