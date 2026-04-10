package com.refoodio.core.domain.model.home

import com.refoodio.core.domain.model.recipe.FoodCategory

data class HomeStats(
    val totalCount: Int = 0,
    val expiringSoonCount: Int = 0,   // ≤3 gün
    val expiringTodayCount: Int = 0,  // bugün bozulacak
    val totalValue: Double = 0.0,     // tüm ürünlerin toplam fiyatı
    val atRiskValue: Double = 0.0,    // yakında bozulacak ürünlerin değeri
    val wasteRiskPercent: Float = 0f, // expiringSoon / total
    val categoryDistribution: List<Pair<FoodCategory, Int>> = emptyList(),
    val storeBreakdown: List<Pair<String, Double>> = emptyList(),
    val expiryBuckets: ExpiryBuckets = ExpiryBuckets()
)

data class ExpiryBuckets(
    val today: Int = 0,      // bugün
    val thisWeek: Int = 0,   // 1–7 gün
    val thisMonth: Int = 0,  // 8–30 gün
    val later: Int = 0       // >30 gün
)
