package com.refoodio.core.data.repository.receipt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.refoodio.core.data.BuildConfig
import java.io.ByteArrayOutputStream
import com.refoodio.core.data.mapper.receipt.parseReceiptResponse
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.ReceiptRepository
import com.refoodio.core.network.datasource.GeminiDataSource
import com.refoodio.core.network.dto.recipe.Content
import com.refoodio.core.network.dto.recipe.GeminiRequest
import com.refoodio.core.network.dto.recipe.InlineData
import com.refoodio.core.network.dto.recipe.Part
import com.refoodio.core.network.exception.ApiException
import kotlinx.coroutines.delay
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(
    private val geminiApi: GeminiDataSource
) : ReceiptRepository {

    companion object {
        private val MODELS = listOf("gemini-3-flash-preview","gemini-2.0-flash", "gemini-2.0-flash-lite")
        private const val MAX_RETRIES = 2
        private const val RETRY_DELAY_MS = 5_000L
        private const val TAG = "ReceiptRepository"
    }

    override suspend fun scanReceipt(imageBytes: ByteArray): Result<List<InventoryItem>> {
        val compressedBytes = compressImage(imageBytes)
        val base64Image = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
        val request = buildRequest(base64Image)

        //BİR GEMİNİ MODELİ BAŞARISIZ OLUNCA DİĞER MODELLERİ DENEME DÖNGÜSÜ
      //  for (model in MODELS) {
            val result = attemptWithRetry(MODELS[0], request)
            if (result.isSuccess) return result
            val error = result.exceptionOrNull()
            if (error is ApiException && error.isFatal) {
                // Kurtarılamaz hata, devam etme
                return result
            }
            Log.w(TAG, "Model $MODELS[0] başarısız, sıradaki deneniyor: ${error?.message}")
        //}

        return Result.failure(Exception("Tüm modeller yanıt vermedi. Lütfen tekrar deneyin."))
    }

    private suspend fun attemptWithRetry(model: String, request: GeminiRequest): Result<List<InventoryItem>> {
        repeat(MAX_RETRIES) { attempt ->
            try {
                val response = geminiApi.generateContent(
                    model = model,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )
                return Result.success(parseReceiptResponse(response))
            } catch (e: ApiException) {
                if (e.isRetryable) {
                    val waitMs = RETRY_DELAY_MS * (attempt + 1)
                    Log.w(TAG, "[$model] ${e.code} hatası, ${waitMs}ms beklenip tekrar denenecek (${attempt + 1}/$MAX_RETRIES)")
                    delay(waitMs)
                } else {
                    return Result.failure(e)
                }
            } catch (e: Exception) {
                if (attempt == MAX_RETRIES - 1) return Result.failure(e)
                delay(RETRY_DELAY_MS)
            }
        }
        return Result.failure(Exception("[$model] $MAX_RETRIES denemede yanıt alınamadı."))
    }

    private fun buildRequest(base64Image: String) = GeminiRequest(
        contents = listOf(
            Content(
                parts = listOf(
                    Part(text = buildReceiptPrompt()),
                    Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                )
            )
        )
    )

    private fun compressImage(bytes: ByteArray, maxWidth: Int = 1024, quality: Int = 70): ByteArray {
        val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return bytes
        val scale = if (original.width > maxWidth) maxWidth.toFloat() / original.width else 1f
        val scaled = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                original,
                (original.width * scale).toInt(),
                (original.height * scale).toInt(),
                true
            )
        } else original
        return ByteArrayOutputStream().also { out ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }.toByteArray()
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
            "shelfLifeDays": <tahmini raf ömrü gün cinsinden, integer>,
            "price": <ürünün fiyatı TL cinsinden, double, fişte yoksa null>,
            "storeName": "<fişin üstündeki market/mağaza adı, tüm ürünler için aynı değer, okunamazsa null>"
          }
        ]

        Kurallar:
        - Miktarı fişten oku, yazılmamışsa 1 kullan.
        - Birimi ürün tipine göre tahmin et (örn: süt → LITER, ekmek → PIECE).
        - Kategoriyi ürün adından belirle.
        - shelfLifeDays için tipik raf ömrünü kullan (süt: 7, ekmek: 3, makarna: 365 gibi).
        - price alanına ürünün satır fiyatını yaz (KDV dahil toplam satır tutarı). Fişte fiyat yoksa null yaz.
        - storeName alanına fişin en üstündeki market adını yaz (örn: "Migros", "A101", "BİM", "CarrefourSA"). Tüm ürünler için aynı değer olacak. Okunamazsa null yaz.
        - Fiş kodu, indirim kalemi, poşet ücreti gibi yiyecek olmayan kalemleri listeye ekleme.
    """.trimIndent()
}
