package com.refoodio.core.data.repository.receipt

import android.util.Base64
import com.refoodio.core.data.BuildConfig
import com.refoodio.core.data.mapper.receipt.parseReceiptResponse
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.ReceiptRepository
import com.refoodio.core.network.api.GeminiApiService
import com.refoodio.core.network.dto.recipe.Content
import com.refoodio.core.network.dto.recipe.GeminiRequest
import com.refoodio.core.network.dto.recipe.InlineData
import com.refoodio.core.network.dto.recipe.Part
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(
    private val geminiApi: GeminiApiService
) : ReceiptRepository {

    override suspend fun scanReceipt(imageBytes: ByteArray): Result<List<InventoryItem>> {
        return try {
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = buildReceiptPrompt()),
                            Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                        )
                    )
                )
            )

            val response = geminiApi.generateContent(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            val items = parseReceiptResponse(response)
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildReceiptPrompt(): String = """
        Bu bir market fişi fotoğrafıdır. Fişteki tüm ürünleri analiz et.
        Her ürün için aşağıdaki JSON formatında bir liste döndür.
        Sadece JSON döndür, başka hiçbir şey ekleme.

        Format:
        [
          {
            "name": "Ürün adı (Türkçe, kısa ve anlaşılır)",
            "quantity": <sayısal miktar, double>,
            "unit": "<GRAM|KILOGRAM|LITER|MILLILITER|PIECE|BUNCH|PACK|CUP|TABLESPOON|TEASPOON>",
            "category": "<DAIRY|VEGETABLE|FRUIT|MEAT_POULTRY|SEAFOOD|DELI|STAPLE_FOOD|GRAINS|LEGUMES|BAKERY|PASTRY|OIL|SAUCE|VINEGAR|SPICE|SEEDS|BREAKFAST|BEVERAGE|CANNED|NUTS|FERMENTED|SWEETENER|OTHER>",
            "shelfLifeDays": <tahmini raf ömrü gün cinsinden, integer>
          }
        ]

        Kurallar:
        - Miktarı fişten oku, yazılmamışsa 1 kullan.
        - Birimi ürün tipine göre tahmin et (örn: süt → LITER, ekmek → PIECE).
        - Kategoriyi ürün adından belirle.
        - shelfLifeDays için tipik raf ömrünü kullan (süt: 7, ekmek: 3, makarna: 365 gibi).
        - Fiş kodu, indirim kalemi, poşet ücreti gibi yiyecek olmayan kalemleri listeye ekleme.
    """.trimIndent()
}
