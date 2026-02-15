package com.refoodio.core.domain.repository

interface FoodCatalogRepository {
    suspend fun loadFoodCatalog()
}