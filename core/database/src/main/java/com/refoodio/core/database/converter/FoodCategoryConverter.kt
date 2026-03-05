package com.refoodio.core.database.converter

import androidx.room.TypeConverter
import com.refoodio.core.domain.model.recipe.FoodCategory

class FoodCategoryConverter {
    @TypeConverter
    fun fromFoodCategory(category: FoodCategory): Int {
        return category.id
    }

    @TypeConverter
    fun toFoodCategory(id: Int): FoodCategory {
        return FoodCategory.fromId(id)
    }
}