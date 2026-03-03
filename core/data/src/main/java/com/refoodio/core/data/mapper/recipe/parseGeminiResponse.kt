package com.refoodio.core.data.mapper.recipe

import com.refoodio.core.data.remote.recipe.RecipeDto
import com.refoodio.core.domain.model.recipe.Recipe
import com.refoodio.core.network.dto.recipe.GeminiResponse
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }
fun parseGeminiResponse(response: GeminiResponse): Recipe {
/*    val rawJson =
        response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.removeSurrounding(
                "```json",
                "```"
            )?.trim() ?: throw Exception("Yanıt boş")*/

    val rawText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        ?: throw Exception("API yanıtı boş.")
    // Markdown işaretlerini (```json ve ```) daha agresif temizleyelim
    val cleanedJson = rawText
        .replace("```json", "")
        .replace("```", "")
        .trim()

    // 1. Önce Data modülündeki DTO'ya çevir (Serialization burada çalışır)
    val recipeDto = json.decodeFromString<RecipeDto>(cleanedJson)

    // 2. Sonra Domain modeline map'le (Domain temiz kalır)
    return recipeDto.toDomain()
}