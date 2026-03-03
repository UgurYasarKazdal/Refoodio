package com.refoodio.core.network.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    val status: Int, val product: ProductDto? = null
)

@Serializable
data class ProductDto(
    val product_name: String? = null,
    val brands: String? = null,
    val categories: String? = null,
    val image_url: String? = null,
    val product_name_tr: String? = null,

    )