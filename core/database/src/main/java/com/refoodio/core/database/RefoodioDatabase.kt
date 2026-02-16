package com.refoodio.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.refoodio.core.database.converter.StringListTypeConverter
import com.refoodio.core.database.dao.catalog.FoodCatalogDao
import com.refoodio.core.database.dao.catalog.FoodSuggestionDao
import com.refoodio.core.database.dao.inventory.InventoryDao
import com.refoodio.core.database.entity.catalog.FoodCatalogFtsEntity
import com.refoodio.core.database.entity.catalog.FoodCatalogItemEntity
import com.refoodio.core.database.entity.inventory.InventoryEntity

@Database(
    entities = [
        InventoryEntity::class,
        FoodCatalogItemEntity::class,
        FoodCatalogFtsEntity::class],
    version = 2
)
@TypeConverters(StringListTypeConverter::class)
abstract class RefoodioDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao

    abstract fun foodCatalogDao(): FoodCatalogDao

    abstract fun foodSuggestionDao(): FoodSuggestionDao

}