package com.refoodio.core.domain.repository.recipe

import com.refoodio.core.domain.model.recipe.Recipe
import com.refoodio.core.domain.model.recipe.RecipePreferences

interface RecipeRepository {
    /**
     * Seçilen malzemeler ve tercihlere göre Gemini API'den tarif getirir.
     * Race condition uyarısı: Parametreler snapshot olarak gönderilir.
     */
    suspend fun getRecipe(
        ingredients: List<String>,
        preferences: RecipePreferences
    ): Result<Recipe>
}