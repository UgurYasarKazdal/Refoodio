package com.refoodio.core.database.converter

import androidx.room.TypeConverter
import com.refoodio.core.domain.model.inventory.FoodUnit

class FoodUnitConverter {
    @TypeConverter
    fun fromFoodUnit(unit: FoodUnit): Int {
        return unit.id
    }

    @TypeConverter
    fun toFoodUnit(id: Int): FoodUnit {
        return FoodUnit.fromId(id)
    }
}