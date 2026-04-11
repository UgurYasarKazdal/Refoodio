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
        val dietLine = if (prefs.dietOptions.isNotEmpty())
            "Beslenme tercihleri: ${prefs.dietOptions.joinToString(", ")}." else ""
        val styleRule = if (prefs.style == "Gourmet")
            "Sadece en uyumlu malzemeleri seç, prezentasyona önem ver."
        else "Tüm malzemeleri kullanmaya çalış, israfı önle, ama malzemeler birbirine uyumlu değilse uyumlu olmayan(ları) eleyebilirsin."

        val role = if (prefs.style == "Gourmet")
            "Sen profesyonel bir mutfak şefisin."
        else
            "Sen mutfakta yemek yapan birisin."
        return """
            $role
            Malzemeler: ${ingredients.joinToString(", ")}
            Pişirme yöntemi: ${prefs.method}
            Maksimum süre: ${prefs.maxTime} dakika
            Pişme derecesi: ${prefs.doneness}
            Stil: ${prefs.style}
            $dietLine
            Kural: $styleRule
            Lütfen sonucu YALNIZCA şu JSON formatında döndür, başka hiçbir şey ekleme:
            { "title": "...", "ingredientsUsed": [...], "instructions": [...], "duration": "...", "difficulty": "..." }
        """.trimIndent()
    }

}