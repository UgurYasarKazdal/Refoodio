package com.refoodio.core.data.remote.catalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodCatalogItemDto(
    @SerialName("alternative_names")
    val alternativeNames: List<String?>?,
    @SerialName("category")
    val category: String?,
    @SerialName("common_pairings")
    val commonPairings: List<String?>?,
    @SerialName("default_shelf_life")
    val defaultShelfLife: Int?,
    @SerialName("freezable")
    val freezable: Boolean?,
    @SerialName("id")
    val id: Int?,
    @SerialName("is_essential")
    val isEssential: Boolean?,
    @SerialName("is_liquid")
    val isLiquid: Boolean?,
    @SerialName("min_quantity_alert")
    val minQuantityAlert: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("nutritional_highlight")
    val nutritionalHighlight: String?,
    @SerialName("opened_shelf_life")
    val openedShelfLife: Int?,
    @SerialName("originalIndex")
    val originalIndex: Int?,
    @SerialName("recommended_location")
    val recommendedLocation: String?,
    @SerialName("reminder_frequency")
    val reminderFrequency: String?,
    @SerialName("seasonality")
    val seasonality: String?,
    @SerialName("storage_note")
    val storageNote: String?,
    @SerialName("storage_temp_ideal")
    val storageTempIdeal: String?,
    @SerialName("suggested_preparation")
    val suggestedPreparation: String?,
    @SerialName("unit")
    val unit: String?
)