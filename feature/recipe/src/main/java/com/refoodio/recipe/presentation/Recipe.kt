package com.refoodio.recipe.presentation

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val requiredIngredients: List<String>, // "Domates", "Yumurta" gibi
    val instructions: String,
    val imageUrl: String? = null
)