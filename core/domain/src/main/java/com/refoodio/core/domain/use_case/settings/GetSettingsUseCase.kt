package com.refoodio.core.domain.use_case.settings

import com.refoodio.core.domain.model.settings.AppSettings
import com.refoodio.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = repository.getSettings()
}
