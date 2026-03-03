package com.refoodio.core.domain.model.recipe

// Kullanıcının tarif sihirbazındaki seçimleri
data class RecipePreferences(
    val method: String,      // Tencere, Fırın, Airfryer vb.
    val maxTime: Int,        // Dakika cinsinden (20, 45, 60+)
    val style: String        // "Gourmet" veya "Waste-Fighter"
)