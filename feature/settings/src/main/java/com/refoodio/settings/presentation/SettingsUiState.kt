package com.refoodio.settings.presentation

import com.refoodio.core.domain.model.settings.AppSettings
import com.refoodio.core.domain.model.settings.ThemeMode

data class SettingsUiState(
    val isLoading: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val expiryNotificationsEnabled: Boolean = true,
    val notifyDaysBefore: Int = 3,
    val weeklySummaryEnabled: Boolean = false,
    val defaultCookingMethod: String = "Tencere",
    val defaultMaxTime: Int = 30,
    val defaultDoneness: String = "Normal",
    val defaultDietOptions: Set<String> = emptySet(),
    val defaultGourmetMode: Boolean = false,

    // UI state
    val showClearConfirmDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showNotifyDaysDialog: Boolean = false,
    val exportCsvContent: String? = null,   // null = yok, doluysa paylaşım tetiklenir
    val snackbarMessage: String? = null
) {
    fun toAppSettings() = AppSettings(
        themeMode = themeMode,
        expiryNotificationsEnabled = expiryNotificationsEnabled,
        notifyDaysBefore = notifyDaysBefore,
        weeklySummaryEnabled = weeklySummaryEnabled,
        defaultCookingMethod = defaultCookingMethod,
        defaultMaxTime = defaultMaxTime,
        defaultDoneness = defaultDoneness,
        defaultDietOptions = defaultDietOptions,
        defaultGourmetMode = defaultGourmetMode
    )
}
