package com.refoodio.core.domain.model.recipe

data class IngredientItem(
    val id: String,
    val name: String,
    val category: String,
    val isExpiredSoon: Boolean,
    val isSelected: Boolean = false
)

