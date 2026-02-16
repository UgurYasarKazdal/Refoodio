package com.refoodio.core.database.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "food_catalog_fts")
@Fts4(contentEntity = FoodCatalogItemEntity::class) // Ana tabloyu kaynak gösteriyoruz
data class FoodCatalogFtsEntity(
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "category")
    val category: String
)

@Entity(tableName = "food_catalog_items")
data class FoodCatalogItemEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "alternative_names")
    val alternativeNames: List<String>?, // TypeConverter gerektirir

    @ColumnInfo(name = "category")
    val category: String?,

    @ColumnInfo(name = "common_pairings")
    val commonPairings: List<String>?, // TypeConverter gerektirir

    @ColumnInfo(name = "default_shelf_life")
    val defaultShelfLife: Int?,

    @ColumnInfo(name = "freezable")
    val freezable: Boolean?,

    @ColumnInfo(name = "is_essential")
    val isEssential: Boolean?,

    @ColumnInfo(name = "is_liquid")
    val isLiquid: Boolean?,

    @ColumnInfo(name = "min_quantity_alert")
    val minQuantityAlert: Int?,

    @ColumnInfo(name = "nutritional_highlight")
    val nutritionalHighlight: String?,

    @ColumnInfo(name = "opened_shelf_life")
    val openedShelfLife: Int?,

    @ColumnInfo(name = "recommended_location")
    val recommendedLocation: String?,

    @ColumnInfo(name = "reminder_frequency")
    val reminderFrequency: String?,

    @ColumnInfo(name = "seasonality")
    val seasonality: String?,

    @ColumnInfo(name = "storage_note")
    val storageNote: String?,

    @ColumnInfo(name = "storage_temp_ideal")
    val storageTempIdeal: String?,

    @ColumnInfo(name = "suggested_preparation")
    val suggestedPreparation: String?,

    @ColumnInfo(name = "unit")
    val unit: String?
)