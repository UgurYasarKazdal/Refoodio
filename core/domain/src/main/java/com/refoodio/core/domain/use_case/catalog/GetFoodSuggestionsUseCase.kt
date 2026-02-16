package com.refoodio.core.domain.use_case.catalog

import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.repository.FoodCatalogRepository
import kotlinx.coroutines.flow.Flow

class GetFoodSuggestionsUseCase constructor(
    private val repository: FoodCatalogRepository
) {
    operator fun invoke(query: String): Flow<List<FoodItem>> {
        return repository.searchSuggestions(query)
    }
}