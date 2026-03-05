package com.refoodio.core.network.api

import com.refoodio.core.network.dto.inventory.BarcodeInventoryResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApi {
    @GET("api/v2/product/{barcode}.json")
    suspend fun getInventoryByBarcode(
        @Path("barcode") barcode: String
    ): BarcodeInventoryResponseDto
}