package com.refoodio.core.data.mapper.receipt

import com.refoodio.core.data.remote.receipt.ReceiptItemDto
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.domain.util.daysToMillis
import com.refoodio.core.network.dto.recipe.GeminiResponse
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

fun parseReceiptResponse(response: GeminiResponse): List<InventoryItem> {
    val rawText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        ?: throw Exception("API yanıtı boş.")

    val cleanedJson = rawText
        .replace("```json", "")
        .replace("```", "")
        .trim()

    val dtos = json.decodeFromString<List<ReceiptItemDto>>(cleanedJson)

    return dtos.map { dto ->
        val unit = FoodUnit.entries.find {
            it.name.equals(dto.unit, ignoreCase = true)
        } ?: FoodUnit.PIECE

        val category = FoodCategory.entries.find {
            it.name.equals(dto.category, ignoreCase = true)
        } ?: FoodCategory.OTHER

        val expiryDate = System.currentTimeMillis() + dto.shelfLifeDays.daysToMillis

        InventoryItem(
            name = dto.name,
            expiryDate = expiryDate,
            quantity = dto.quantity,
            unit = unit,
            category = category
        )
    }
}
