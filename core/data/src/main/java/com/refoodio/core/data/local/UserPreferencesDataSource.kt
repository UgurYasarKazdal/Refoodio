package com.refoodio.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val FOOD_CATALOG_LOADED = booleanPreferencesKey("food_catalog_loaded")
    }

    val isFoodCatalogLoaded = dataStore.data
        .map { it[Keys.FOOD_CATALOG_LOADED] ?: false }

    suspend fun setFoodCatalogLoaded(isLoaded: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.FOOD_CATALOG_LOADED] = isLoaded
        }
    }
}