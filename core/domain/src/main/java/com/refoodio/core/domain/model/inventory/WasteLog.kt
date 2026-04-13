package com.refoodio.core.domain.model.inventory

data class WasteLog(
    val id: Int = 0,
    val name: String,
    val quantity: Double,
    val unit: FoodUnit,
    val wastedAt: Long = System.currentTimeMillis()
)
