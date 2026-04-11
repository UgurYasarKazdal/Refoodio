package com.refoodio.core.domain.use_case.settings

import com.refoodio.core.domain.repository.SettingsRepository

class ClearInventoryUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.clearAllInventory()
}
