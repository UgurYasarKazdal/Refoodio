package com.refoodio.recipe.presentation

import com.refoodio.core.domain.model.recipe.IngredientItem
import com.refoodio.core.domain.model.recipe.Recipe

data class RecipeWizardUiState(
    val isLoading: Boolean = false,
    val ingredients: List<IngredientItem> = emptyList(),
    val selectedCookingMethod: String = "Airfryer",
    val selectedTime: Int = 30,
    val cookingDifficulty: String = "Kolay",
    val isGourmetMode: Boolean = false,
    val generatedRecipe: Recipe? = null, // Gemini'den gelen sonuç
    val errorMessage: String? = null
)