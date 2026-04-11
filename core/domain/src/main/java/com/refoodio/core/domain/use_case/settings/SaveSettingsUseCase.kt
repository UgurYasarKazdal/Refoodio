package com.refoodio.core.domain.use_case.settings

import com.refoodio.core.domain.model.settings.AppSettings
import com.refoodio.core.domain.repository.SettingsRepository

class SaveSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(settings: AppSettings) = repository.saveSettings(settings)
}
