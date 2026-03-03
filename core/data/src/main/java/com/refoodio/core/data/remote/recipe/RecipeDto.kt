package com.refoodio.core.data.remote.recipe

import kotlinx.serialization.Serializable

// com.refoodio.data.remote.dto.RecipeDto
@Serializable
data class RecipeDto(
    val title: String,
    val ingredientsUsed: List<String>,
    val instructions: List<String>,
    val duration: String,
    val difficulty: String
)

// Gemini'ye özel diğer DTO'lar (GeminiResponse vb.) da burada kalır.