package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.settings.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun saveSettings(settings: AppSettings)
    suspend fun clearAllInventory()
    fun exportInventoryCsv(): Flow<String>
}
