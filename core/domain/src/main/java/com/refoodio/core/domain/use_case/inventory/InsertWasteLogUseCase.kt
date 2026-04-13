package com.refoodio.core.domain.use_case.inventory

import com.refoodio.core.domain.model.inventory.WasteLog
import com.refoodio.core.domain.repository.WasteLogRepository

class InsertWasteLogUseCase(
    private val repository: WasteLogRepository
) {
    suspend operator fun invoke(wasteLog: WasteLog) = repository.insert(wasteLog)
}
