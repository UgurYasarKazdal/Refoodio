package com.refoodio.core.domain.model.inventory

import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.domain.util.daysToMillis

data class InventoryItem(
    val id: Int = 0,
    val name: String,
    val expiryDate: Long,
    val quantity: Double,
    val unit: FoodUnit,
    val category: FoodCategory
) {
    fun isNearExpiry(): Boolean {
        val threeDaysInMillis = 3.daysToMillis
        return (expiryDate - System.currentTimeMillis()) < threeDaysInMillis
    }
}