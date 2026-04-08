package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.inventory.InventoryItem

interface ReceiptRepository {
    suspend fun scanReceipt(imageBytes: ByteArray): Result<List<InventoryItem>>
}
