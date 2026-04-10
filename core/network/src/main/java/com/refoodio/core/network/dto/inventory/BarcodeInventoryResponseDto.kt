package com.refoodio.core.network.dto.inventory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarcodeInventoryResponseDto(
    val status: Int,
    @SerialName("product") val barcodeInventory: BarcodeInventoryDto? = null
)

@Serializable
data class BarcodeInventoryDto(
    val product_name: String? = null,
    val brands: String? = null,
    val categories: String? = null,
    val image_url: String? = null,
    val product_name_tr: String? = null
)