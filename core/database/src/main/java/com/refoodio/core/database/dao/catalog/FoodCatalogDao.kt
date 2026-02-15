package com.refoodio.core.database.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.refoodio.core.database.entity.catalog.FoodCatalogItemEntity

@Dao
interface FoodCatalogDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(items: List<FoodCatalogItemEntity>)
}