package com.refoodio.core.data.repository.inventory

import com.refoodio.core.database.dao.waste.WasteLogDao
import com.refoodio.core.database.entity.waste.WasteLogEntity
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.WasteLog
import com.refoodio.core.domain.repository.WasteLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WasteLogRepositoryImpl @Inject constructor(
    private val dao: WasteLogDao
) : WasteLogRepository {

    override suspend fun insert(wasteLog: WasteLog) {
        dao.insert(
            WasteLogEntity(
                id = wasteLog.id,
                name = wasteLog.name,
                quantity = wasteLog.quantity,
                unitId = wasteLog.unit.id,
                wastedAt = wasteLog.wastedAt
            )
        )
    }

    override fun getAll(): Flow<List<WasteLog>> =
        dao.getAll().map { entities ->
            entities.map { entity ->
                WasteLog(
                    id = entity.id,
                    name = entity.name,
                    quantity = entity.quantity,
                    unit = FoodUnit.fromId(entity.unitId),
                    wastedAt = entity.wastedAt
                )
            }
        }
}
