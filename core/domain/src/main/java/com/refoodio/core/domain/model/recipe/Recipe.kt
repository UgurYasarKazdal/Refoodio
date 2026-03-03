package com.refoodio.core.domain.model.recipe

data class Recipe(
    val title: String,
    val ingredientsUsed: List<String>,
    val instructions: List<String>,
    val duration: String,
    val difficulty: String
)