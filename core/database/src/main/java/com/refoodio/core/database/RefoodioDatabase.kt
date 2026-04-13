package com.refoodio.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.refoodio.core.database.converter.FoodCategoryConverter
import com.refoodio.core.database.converter.FoodUnitConverter
import com.refoodio.core.database.converter.StringListTypeConverter
import com.refoodio.core.database.dao.catalog.FoodCatalogDao
import com.refoodio.core.database.dao.catalog.FoodSuggestionDao
import com.refoodio.core.database.dao.inventory.InventoryDao
import com.refoodio.core.database.dao.waste.WasteLogDao
import com.refoodio.core.database.entity.catalog.FoodCatalogFtsEntity
import com.refoodio.core.database.entity.catalog.FoodCatalogItemEntity
import com.refoodio.core.database.entity.inventory.InventoryEntity
import com.refoodio.core.database.entity.waste.WasteLogEntity

@Database(
    entities = [
        InventoryEntity::class,
        FoodCatalogItemEntity::class,
        FoodCatalogFtsEntity::class,
        WasteLogEntity::class
    ],
    version = 6
)
@TypeConverters(
    StringListTypeConverter::class, FoodUnitConverter::class, FoodCategoryConverter::class
)
abstract class RefoodioDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
    abstract fun foodCatalogDao(): FoodCatalogDao
    abstract fun foodSuggestionDao(): FoodSuggestionDao
    abstract fun wasteLogDao(): WasteLogDao
}
