package com.refoodio.core.domain.model.catalog

/**
 * Uygulamanın temel gıda öğesi modeli. 
 * Hiçbir kütüphaneye (Room, Serialization vb.) bağımlılığı yoktur.
 */
data class FoodItem(
    val id: Int,
    val name: String,
    val alternativeNames: List<String> = emptyList(),
    val category: String = "",
    val commonPairings: List<String> = emptyList(),
    val defaultShelfLife: Int = 0,
    val openedShelfLife: Int? = null,
    val isFreezable: Boolean = false,
    val isEssential: Boolean = false,
    val isLiquid: Boolean = false,
    val minQuantityAlert: Int = 1,
    val nutritionalHighlight: String = "",
    val recommendedLocation: String = "",
    val storageNote: String = "",
    val unit: String = ""
)