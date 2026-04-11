package com.refoodio.core.domain.model.recipe

// Kullanıcının tarif sihirbazındaki seçimleri
data class RecipePreferences(
    val method: String,              // Tencere, Fırın, Airfryer vb.
    val maxTime: Int,                // Dakika cinsinden
    val style: String,               // "Gourmet" veya "Waste-Fighter"
    val doneness: String = "Normal", // Az Pişmiş / Normal / İyice Pişmiş
    val dietOptions: List<String> = emptyList() // Düşük Kalori, Yüksek Protein vb.
)