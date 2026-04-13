package com.refoodio.core.database.entity.waste

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waste_log")
data class WasteLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Double,
    val unitId: Int,
    val wastedAt: Long
)
