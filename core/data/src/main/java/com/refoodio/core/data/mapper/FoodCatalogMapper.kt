package com.refoodio.core.data.mapper

import com.refoodio.core.data.remote.FoodCatalogItemDto
import com.refoodio.core.database.entity.catalog.FoodCatalogItemEntity
import com.refoodio.core.domain.model.catalog.FoodItem

// Entity'den Domain Model'e (UI'a veri verirken kullanılır)
fun FoodCatalogItemEntity.toDomain(): FoodItem {
    return FoodItem(
        id = this.id,
        name = this.name.orEmpty(),
        alternativeNames = this.alternativeNames ?: emptyList(),
        category = this.category.orEmpty(),
        commonPairings = this.commonPairings ?: emptyList(),
        defaultShelfLife = this.defaultShelfLife ?: 0,
        openedShelfLife = this.openedShelfLife,
        isFreezable = this.freezable ?: false,
        isEssential = this.isEssential ?: false,
        isLiquid = this.isLiquid ?: false,
        minQuantityAlert = this.minQuantityAlert ?: 1,
        nutritionalHighlight = this.nutritionalHighlight.orEmpty(),
        recommendedLocation = this.recommendedLocation.orEmpty(),
        storageNote = this.storageNote.orEmpty(),
        unit = this.unit ?: "pcs"
    )
}

// DTO'dan Entity'ye (Database'e kaydederken kullanılır)
fun FoodCatalogItemDto.toEntity(): FoodCatalogItemEntity {
    return FoodCatalogItemEntity(
        id = this.id ?: 0,
        name = this.name,
        alternativeNames = this.alternativeNames?.filterNotNull(),
        category = this.category,
        commonPairings = this.commonPairings?.filterNotNull(),
        defaultShelfLife = this.defaultShelfLife,
        freezable = this.freezable,
        isEssential = this.isEssential,
        isLiquid = this.isLiquid,
        minQuantityAlert = this.minQuantityAlert,
        nutritionalHighlight = this.nutritionalHighlight,
        openedShelfLife = this.openedShelfLife,
        recommendedLocation = this.recommendedLocation,
        reminderFrequency = this.reminderFrequency,
        seasonality = this.seasonality,
        storageNote = this.storageNote,
        storageTempIdeal = this.storageTempIdeal,
        suggestedPreparation = this.suggestedPreparation,
        unit = this.unit
    )
}