package com.refoodio.core.domain.model.recipe

import com.refoodio.core.domain.R

enum class RecipeSection(val titleResId: Int, val iconResId: Int) {
    PRODUCE(R.string.section_produce, R.drawable.ic_section_produce), // Sebze & Meyve
    PROTEIN(R.string.section_protein, R.drawable.ic_section_protein), // Et & Balık
    DAIRY(R.string.section_dairy, R.drawable.ic_section_dairy),     // Süt Ürünleri
    PANTRY(R.string.section_pantry, R.drawable.ic_section_pantry),   // Kiler & Temel Gıda
    OTHER(R.string.section_other, R.drawable.ic_section_other);
}

fun FoodCategory.toRecipeSection(): RecipeSection {
    return when (this) {
        FoodCategory.VEGETABLE, FoodCategory.FRUIT -> RecipeSection.PRODUCE

        FoodCategory.MEAT_POULTRY, FoodCategory.SEAFOOD, FoodCategory.DELI -> RecipeSection.PROTEIN

        FoodCategory.DAIRY -> RecipeSection.DAIRY

        FoodCategory.STAPLE_FOOD, FoodCategory.GRAINS, FoodCategory.LEGUMES, FoodCategory.BAKERY, FoodCategory.PASTRY, FoodCategory.OIL, FoodCategory.SAUCE, FoodCategory.VINEGAR, FoodCategory.SPICE, FoodCategory.SEEDS, FoodCategory.CANNED, FoodCategory.FERMENTED, FoodCategory.SWEETENER, FoodCategory.NUTS, FoodCategory.BREAKFAST, FoodCategory.BEVERAGE -> RecipeSection.PANTRY

        else -> RecipeSection.OTHER
    }
}