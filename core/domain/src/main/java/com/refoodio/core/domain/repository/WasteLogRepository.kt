package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.inventory.WasteLog
import kotlinx.coroutines.flow.Flow

interface WasteLogRepository {
    suspend fun insert(wasteLog: WasteLog)
    fun getAll(): Flow<List<WasteLog>>
}
