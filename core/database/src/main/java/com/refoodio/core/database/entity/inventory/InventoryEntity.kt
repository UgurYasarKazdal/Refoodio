package com.refoodio.core.database.entity.inventory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.recipe.FoodCategory

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val expiryDate: Long,
    val quantity: Double,
    @ColumnInfo(name = "unit_id") val unit: FoodUnit,
    @ColumnInfo(name = "category_id") val category: FoodCategory,
    val price: Double? = null,
    @ColumnInfo(name = "store_name") val storeName: String? = null
)