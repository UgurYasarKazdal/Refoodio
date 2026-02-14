package com.refoodio.core.domain.model.inventory

data class InventoryItem(
    val id: Int = 0,
    val name: String,
    val expiryDate: Long,
    val quantity: Double
) {
    // İş mantığı fonksiyonlarını buraya ekleyebiliriz
    fun isNearExpiry(): Boolean {
        val threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L
        return (expiryDate - System.currentTimeMillis()) < threeDaysInMillis
    }
}