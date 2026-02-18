package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.catalog.FoodItem
import kotlinx.coroutines.flow.Flow

interface FoodCatalogRepository {
    suspend fun loadFoodCatalog()

    fun searchSuggestions(query: String): Flow<List<FoodItem>>
}