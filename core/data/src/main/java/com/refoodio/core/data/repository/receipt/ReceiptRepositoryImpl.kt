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
        private val MODELS = listOf("gemini-2.0-flash", "gemini-2.0-flash-lite", "gemini-1.5-flash")
        private const val MAX_RETRIES = 2
        private const val RETRY_DELAY_MS = 5_000L
        private const val TAG = "ReceiptRepository"
    }

    override suspend fun scanReceipt(imageBytes: ByteArray): Result<List<InventoryItem>> {
        val compressedBytes = compressImage(imageBytes)
        val base64Image = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
        val request = buildRequest(base64Image)

        // BİR GEMİNİ MODELİ BAŞARISIZ OLUNCA DİĞER MODELLERİ DENEME DÖNGÜSÜ
        for (model in MODELS) {
            val result = attemptWithRetry(model, request)
            if (result.isSuccess) return result
            
            val error = result.exceptionOrNull()
            if (error is ApiException && error.isFatal) {
                // Kurtarılamaz hata (örn: geçersiz API anahtarı), devam etme
                return result
            }
            Log.w(TAG, "Model $model başarısız, sıradaki deneniyor: ${error?.message}")
        }

        return Result.failure(Exception("Tüm modeller yanıt vermedi. Lütfen internet bağlantınızı ve API anahtarınızı kontrol edin."))
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
            } catch (e: Exception) {
                // ApiException ise ve tekrar denenebilirse bekle
                if (e is ApiException && e.isRetryable) {
                    val waitMs = RETRY_DELAY_MS * (attempt + 1)
                    Log.w(TAG, "[$model] ${e.code} hatası, ${waitMs}ms beklenip tekrar denenecek (${attempt + 1}/$MAX_RETRIES)")
                    delay(waitMs)
                } else if (e is ApiException) {
                    // Tekrar denenemez bir API hatası (Fatal)
                    return Result.failure(e)
                } else {
                    // Bilinmeyen bir exception (Network vb)
                    if (attempt == MAX_RETRIES - 1) return Result.failure(e)
                    delay(RETRY_DELAY_MS)
                }
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
        Bu bir market fişi fotoğrafıdır. Fişteki tüm gıda ürünlerini analiz et.
        Her ürün için aşağıdaki JSON formatında bir liste döndür.
        Sadece JSON döndür, başka hiçbir şey ekleme.

        Format:
        [
          {
            "name": "Ürün adı (Türkçe, kısa ve anlaşılır — paket/kutu/şişe gibi ambalaj ifadelerini isme ekleme)",
            "quantity": <sayısal miktar, double>,
            "unit": "<GRAM|KILOGRAM|LITER|MILLILITER|PIECE|BUNCH|PACK>",
            "category": "<DAIRY|VEGETABLE|FRUIT|MEAT_POULTRY|SEAFOOD|DELI|STAPLE_FOOD|GRAINS|LEGUMES|BAKERY|PASTRY|OIL|SAUCE|VINEGAR|SPICE|SEEDS|BREAKFAST|BEVERAGE|CANNED|NUTS|FERMENTED|SWEETENER|OTHER>",
            "shelfLifeDays": <tahmini raf ömrü gün cinsinden, integer>,
            "price": <ürünün satır fiyatı TL cinsinden, double, fişte yoksa null>,
            "storeName": "<fişin üstündeki market adı, tüm ürünler için aynı, okunamazsa null>"
          }
        ]

    MİKTAR VE BİRİM KURALLARI — ÇOK ÖNEMLİ:
        Hedef: Kullanıcının mutfakta düşündüğü birimde kaydet, satın alma biriminde değil.

        1. Ürün adında ADET/KAÇ'LI bilgisi varsa → PIECE kullan, adedi quantity yap:
           - "20'li Yumurta"      → quantity: 20,   unit: PIECE
           - "6'lı Yoğurt"        → quantity: 6,    unit: PIECE
           - "12'li Meyve Suyu"   → quantity: 12,   unit: PIECE
           - "30'lu Yumurta"      → quantity: 30,   unit: PIECE

        2. Ürün adında veya fişte AĞIRLIK bilgisi varsa → GRAM veya KILOGRAM kullan:
           - "500g Tuz"            → quantity: 500,  unit: GRAM
           - "1 kg Un"             → quantity: 1,    unit: KILOGRAM
           - "250g Tereyağı"       → quantity: 250,  unit: GRAM
           - "3 kg Kıyma"          → quantity: 3,    unit: KILOGRAM
           - "Makarna 500g"        → quantity: 500,  unit: GRAM

        3. Ürün adında veya fişte HACİM bilgisi varsa → LITER veya MILLILITER kullan:
           - "1L Süt"              → quantity: 1,    unit: LITER
           - "500ml Ayran"         → quantity: 500,  unit: MILLILITER
           - "5L Zeytinyağı"       → quantity: 5,    unit: LITER
           - "330ml Kola"          → quantity: 330,  unit: MILLILITER

        4. Doğal olarak tek tek sayılan gıdalar → PIECE:
           - Ekmek, simit, baget, pide → quantity: 1, unit: PIECE
           - Domates, biber, patlıcan (adet satılanlar) → PIECE

        5. Tartı ile satılan sebze/meyve → KILOGRAM veya GRAM:
           - "Domates 1.250 kg"    → quantity: 1.25, unit: KILOGRAM
           - "Muz 800g"            → quantity: 800,  unit: GRAM

        6. Hiçbir miktar bilgisi çıkarılamıyorsa → PACK kullan, quantity: 1

        DİĞER KURALLAR:
        - Ürün adını kısa tut. "20'li", "500g", "1L" gibi miktar ifadelerini isme ekleme, zaten quantity/unit alanlarına giriyor.
        - Kategoriyi ürün adından belirle.
        - shelfLifeDays: süt→7, yumurta→21, ekmek→3, makarna→365, kıyma→2, sebze→7, meyve→7
        - price: satır fiyatını yaz (KDV dahil). Yoksa null.
        - storeName: fişin tepesindeki market adı. Tüm ürünler için aynı. Okunamazsa null.
        - Fiş kodu, indirim, poşet ücreti, KDV satırı gibi gıda olmayan kalemleri ekleme.
    """.trimIndent()
}
