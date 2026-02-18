package com.refoodio.core.data.repository.catalog

import android.content.Context
import android.util.Log
import com.refoodio.core.data.local.UserPreferencesDataSource
import com.refoodio.core.data.mapper.catalog.toDomain
import com.refoodio.core.data.mapper.catalog.toEntity
import com.refoodio.core.data.remote.catalog.FoodCatalogItemDto
import com.refoodio.core.database.dao.catalog.FoodCatalogDao
import com.refoodio.core.database.dao.catalog.FoodSuggestionDao
import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.repository.FoodCatalogRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class FoodCatalogRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPrefs: UserPreferencesDataSource,
    private val dao: FoodCatalogDao,
    private val sdao: FoodSuggestionDao
) : FoodCatalogRepository {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadFoodCatalog() {
        val isLoaded = userPrefs.isFoodCatalogLoaded.first()
        val count = dao.getCount()
        if (isLoaded && count > 0) return

        withContext(Dispatchers.IO) {
            runCatching {
                val inputStream =
                    context.assets.open("food_catalog.json")

                val dtos = inputStream.use {
                    Json.decodeFromStream<List<FoodCatalogItemDto>>(it)
                }
                if (dtos.isNotEmpty()) {
                    dao.insertAll(dtos.map { it.toEntity() })
                    userPrefs.setFoodCatalogLoaded(true)
                }
            }.onFailure { e ->
                Log.e("FoodCatalog", "Katalog yüklenirken hata oluştu", e)
            }
        }
    }

    override fun searchSuggestions(query: String): Flow<List<FoodItem>> {
        return sdao.searchSuggestions(query).map {
            it.map { it.toDomain() }
        }
    }
}