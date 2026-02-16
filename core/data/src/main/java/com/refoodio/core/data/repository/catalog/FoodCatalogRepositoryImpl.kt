package com.refoodio.core.data.repository.catalog

import android.content.Context
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
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FoodCatalogRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context, // <-- Bu notasyon eksik!
    private val userPrefs: UserPreferencesDataSource, // Yeni merkezi kaynağımız
    private val dao: FoodCatalogDao,
    private val sdao: FoodSuggestionDao
) : FoodCatalogRepository {

    override suspend fun loadFoodCatalog() {
        // 1. Adım: Veri daha önce yüklendi mi? (DataSource üzerinden kontrol)
        // first() kullanarak Flow'dan o anki ilk değeri alıyoruz.
        val isLoaded = userPrefs.isFoodCatalogLoaded.first()
        val count = dao.getCount()

        if (!isLoaded || count == 0) {
            try {
                withContext(Dispatchers.IO) {
                    // 2. Adım: JSON dosyasını işle
                    val jsonString = context.assets.open("food_catalog.json").bufferedReader()
                        .use { it.readText() }
                    val dtos = Json.Default.decodeFromString<List<FoodCatalogItemDto>>(jsonString)

                    // 3. Adım: Veritabanına kaydet
                    dao.insertAll(dtos.map { it.toEntity() })

                    // 4. Adım: Başarılıysa DataStore bayrağını güncelle
                    userPrefs.setFoodCatalogLoaded(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun searchSuggestions(query: String): Flow<List<FoodItem>> {
        return sdao.searchSuggestions(query).map {
            it.map { it.toDomain() }
        }
    }
}