package com.refoodio.core.network.api

import com.refoodio.core.network.dto.inventory.ProductResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApi {
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): ProductResponseDto
}