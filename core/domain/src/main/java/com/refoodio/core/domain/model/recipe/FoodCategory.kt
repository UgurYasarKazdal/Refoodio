package com.refoodio.core.domain.model.recipe

import com.refoodio.core.domain.R

enum class FoodCategory(
    val id: Int, val nameResId: Int
) {
    DAIRY(1, R.string.cat_dairy), VEGETABLE(2, R.string.cat_vegetable), FRUIT(
        3, R.string.cat_fruit
    ),
    MEAT_POULTRY(4, R.string.cat_meat_poultry), SEAFOOD(5, R.string.cat_seafood), DELI(
        6, R.string.cat_deli
    ),
    STAPLE_FOOD(7, R.string.cat_staple_food), GRAINS(8, R.string.cat_grains), LEGUMES(
        9, R.string.cat_legumes
    ),
    BAKERY(10, R.string.cat_bakery), PASTRY(11, R.string.cat_pastry), OIL(
        12, R.string.cat_oil
    ),
    SAUCE(13, R.string.cat_sauce), VINEGAR(14, R.string.cat_vinegar), SPICE(
        15, R.string.cat_spice
    ),
    SEEDS(16, R.string.cat_seeds), BREAKFAST(17, R.string.cat_breakfast), BEVERAGE(
        18, R.string.cat_beverage
    ),
    CANNED(19, R.string.cat_canned), NUTS(20, R.string.cat_nuts), FERMENTED(
        21, R.string.cat_fermented
    ),
    SWEETENER(22, R.string.cat_sweetener), OTHER(23, R.string.cat_other);

    companion object {
        fun fromId(id: Int): FoodCategory = values().find { it.id == id } ?: OTHER
    }
}