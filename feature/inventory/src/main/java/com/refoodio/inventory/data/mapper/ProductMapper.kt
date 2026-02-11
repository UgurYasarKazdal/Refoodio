package com.refoodio.inventory.data.mapper

import com.refoodio.inventory.data.local.ProductEntity
import com.refoodio.inventory.domain.model.Product

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        expiryDate = expiryDate,
        quantity = quantity
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        expiryDate = expiryDate,
        quantity = quantity
    )
}