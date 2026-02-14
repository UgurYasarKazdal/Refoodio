package com.refoodio.core.database.entity.inventory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val expiryDate: Long,
    val quantity: Double,
)