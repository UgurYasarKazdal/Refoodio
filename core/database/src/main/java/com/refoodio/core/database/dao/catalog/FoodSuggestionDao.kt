package com.refoodio.core.database.dao.catalog

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.refoodio.core.database.entity.catalog.FoodCatalogItemEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FoodSuggestionDao {
    @Transaction
    @Query(
        """
        SELECT * FROM food_catalog_items 
        JOIN food_catalog_fts ON food_catalog_items.id = food_catalog_fts.rowid 
        WHERE food_catalog_fts MATCH :query
    """
    )
    fun searchSuggestions(query: String): Flow<List<FoodCatalogItemEntity>>
}