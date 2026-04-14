package com.refoodio.core.data.mapper.inventory

import com.refoodio.core.database.entity.inventory.InventoryEntity
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem

fun InventoryEntity.toDomain(): InventoryItem {
    return InventoryItem(
        id = id,
        name = name,
        expiryDate = expiryDate,
        quantity = quantity,
        unit = unit,
        category = category,
        price = price,
        storeName = storeName,
        sideUnits = parseSideUnits(sideUnits)
    )
}

fun InventoryItem.toEntity(): InventoryEntity {
    return InventoryEntity(
        id = id,
        name = name,
        expiryDate = expiryDate,
        quantity = quantity,
        unit = unit,
        category = category,
        price = price,
        storeName = storeName,
        sideUnits = sideUnits.joinToString(",") { it.id.toString() }
    )
}

private fun parseSideUnits(raw: String): List<FoodUnit> {
    if (raw.isBlank()) return emptyList()
    return raw.split(",").mapNotNull { segment ->
        segment.trim().toIntOrNull()?.let { id ->
            FoodUnit.entries.find { it.id == id }
        }
    }
}
