package com.refoodio.core.data.remote.receipt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptItemDto(
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String,
    @SerialName("shelfLifeDays") val shelfLifeDays: Int = 7,
    val price: Double? = null,
    @SerialName("storeName") val storeName: String? = null
)
