package com.refoodio.core.domain.use_case.settings

import com.refoodio.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ExportInventoryUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<String> = repository.exportInventoryCsv()
}
