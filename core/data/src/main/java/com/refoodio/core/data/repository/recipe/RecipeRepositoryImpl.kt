package com.refoodio.core.data.repository.recipe

import com.refoodio.core.data.BuildConfig
import com.refoodio.core.data.mapper.recipe.parseGeminiResponse
import com.refoodio.core.domain.model.recipe.Recipe
import com.refoodio.core.domain.model.recipe.RecipePreferences
import com.refoodio.core.domain.repository.recipe.RecipeRepository
import com.refoodio.core.network.datasource.GeminiDataSource
import com.refoodio.core.network.dto.recipe.Content
import com.refoodio.core.network.dto.recipe.GeminiRequest
import com.refoodio.core.network.dto.recipe.Part
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val geminiApi: GeminiDataSource
) : RecipeRepository {

    override suspend fun getRecipe(
        ingredients: List<String>,
        preferences: RecipePreferences
    ): Result<Recipe> {
        return try {
            // 1. Prompt Hazırla
            val promptText = buildPrompt(ingredients, preferences)

            // 2. İstek Oluştur
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = promptText))))
            )

            // 3. API Çağrısı Yap (generateContent)
            val response = geminiApi.generateContent(
                model = "gemini-3-flash-preview",
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            // 4. Yanıtı Parse Et ve Sonucu Dön
            val recipe = parseGeminiResponse(response)
            Result.success(recipe)
        } catch (e: Exception) {
            // Ağ hataları veya API limitleri burada yakalanır
            Result.failure(e)
        }
    }

    private fun buildPrompt(ingredients: List<String>, prefs: RecipePreferences): String {
        return """
            Sen profesyonel bir mutfak şefisin. 
            Malzemeler: ${ingredients.joinToString(", ")}
            Tercihler: ${prefs.method} ile, ${prefs.maxTime} dakikada, ${prefs.style} tarzında.
            
            Kural: ${if (prefs.style == "Gourmet") "Sadece en uyumlu olanları seç." else "Tüm malzemeleri kullanmaya çalış."}
            Lütfen sonucu şu JSON formatında döndür: { "title": "...", "ingredientsUsed": [...], "instructions": [...], "duration": "...", "difficulty": "..." }
        """.trimIndent()
    }

}