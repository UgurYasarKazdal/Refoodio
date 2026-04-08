package com.refoodio.core.domain.use_case.inventory.addInventory

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.ReceiptRepository

class ScanReceiptUseCase(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(imageBytes: ByteArray): Result<List<InventoryItem>> {
        return repository.scanReceipt(imageBytes)
    }
}
