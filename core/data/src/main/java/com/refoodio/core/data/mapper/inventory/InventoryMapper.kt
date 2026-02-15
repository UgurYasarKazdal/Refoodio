package com.refoodio.core.data.mapper.inventory

import com.refoodio.core.database.entity.inventory.InventoryEntity
import com.refoodio.core.domain.model.inventory.InventoryItem

fun InventoryEntity.toDomain(): InventoryItem {
    return InventoryItem(
        id = id,
        name = name,
        expiryDate = expiryDate,
        quantity = quantity
    )
}

fun InventoryItem.toEntity(): InventoryEntity {
    return InventoryEntity(
        id = id,
        name = name,
        expiryDate = expiryDate,
        quantity = quantity
    )
}