package com.refoodio.core.domain.use_case.recipe

import com.refoodio.core.domain.model.recipe.IngredientItem
import com.refoodio.core.domain.model.recipe.Recipe
import com.refoodio.core.domain.model.recipe.RecipePreferences
import com.refoodio.core.domain.repository.recipe.RecipeRepository

class GenerateRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(
        ingredients: List<IngredientItem>,
        method: String,
        maxTime: Int,
        isGourmet: Boolean,
        doneness: String = "Normal",
        dietOptions: List<String> = emptyList()
    ): Result<Recipe> {

        if (ingredients.isEmpty()) {
            return Result.failure(Exception("Seçili malzeme bulunamadı."))
        }

        val ingredientNames = ingredients.map { it.name }

        return recipeRepository.getRecipe(
            ingredients = ingredientNames,
            preferences = RecipePreferences(
                method = method,
                maxTime = maxTime,
                style = if (isGourmet) "Gourmet" else "Waste-Fighter",
                doneness = doneness,
                dietOptions = dietOptions
            )
        )
    }
}