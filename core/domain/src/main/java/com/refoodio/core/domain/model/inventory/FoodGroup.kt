package com.refoodio.core.domain.model.inventory

import com.refoodio.core.domain.R
import com.refoodio.core.domain.model.inventory.FoodUnit.PIECE
import com.refoodio.core.domain.model.recipe.FoodCategory

enum class FoodGroup(val id: Int, val titleResId: Int, val iconResId: Int) {
    PRODUCE(id = 1, R.string.section_produce, R.drawable.ic_section_produce), // Sebze & Meyve
    PROTEIN(id = 2, R.string.section_protein, R.drawable.ic_section_protein), // Et & Balık
    DAIRY(id = 3, R.string.section_dairy, R.drawable.ic_section_dairy),     // Süt Ürünleri
    PANTRY(id = 4, R.string.section_pantry, R.drawable.ic_section_pantry),   // Kiler & Temel Gıda
    OTHER(id = 5, R.string.section_other, R.drawable.ic_section_other);

    companion object {
        fun fromId(id: Int): FoodGroup = FoodGroup.entries.find { it.id == id } ?: OTHER
    }
}

fun FoodCategory.toFoodGroup(): FoodGroup {
    return when (this) {
        FoodCategory.VEGETABLE, FoodCategory.FRUIT -> FoodGroup.PRODUCE

        FoodCategory.MEAT_POULTRY, FoodCategory.SEAFOOD, FoodCategory.DELI -> FoodGroup.PROTEIN

        FoodCategory.DAIRY -> FoodGroup.DAIRY

        FoodCategory.STAPLE_FOOD, FoodCategory.GRAINS, FoodCategory.LEGUMES, FoodCategory.BAKERY, FoodCategory.PASTRY, FoodCategory.OIL, FoodCategory.SAUCE, FoodCategory.VINEGAR, FoodCategory.SPICE, FoodCategory.SEEDS, FoodCategory.CANNED, FoodCategory.FERMENTED, FoodCategory.SWEETENER, FoodCategory.NUTS, FoodCategory.BREAKFAST, FoodCategory.BEVERAGE -> FoodGroup.PANTRY

        else -> FoodGroup.OTHER
    }
}