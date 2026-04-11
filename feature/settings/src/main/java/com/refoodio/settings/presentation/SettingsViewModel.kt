package com.refoodio.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.settings.ThemeMode
import com.refoodio.core.domain.use_case.settings.SettingsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: SettingsUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            useCases.getSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        themeMode = settings.themeMode,
                        expiryNotificationsEnabled = settings.expiryNotificationsEnabled,
                        notifyDaysBefore = settings.notifyDaysBefore,
                        weeklySummaryEnabled = settings.weeklySummaryEnabled,
                        defaultCookingMethod = settings.defaultCookingMethod,
                        defaultDietOptions = settings.defaultDietOptions,
                        defaultGourmetMode = settings.defaultGourmetMode
                    )
                }
            }
        }
    }

    // ── Bildirimler ───────────────────────────────────────────────────────

    fun toggleExpiryNotifications() {
        updateAndSave { it.copy(expiryNotificationsEnabled = !it.expiryNotificationsEnabled) }
    }

    fun toggleWeeklySummary() {
        updateAndSave { it.copy(weeklySummaryEnabled = !it.weeklySummaryEnabled) }
    }

    fun setNotifyDaysBefore(days: Int) {
        updateAndSave { it.copy(notifyDaysBefore = days, showNotifyDaysDialog = false) }
    }

    // ── Görünüm ───────────────────────────────────────────────────────────

    fun setThemeMode(mode: ThemeMode) {
        updateAndSave { it.copy(themeMode = mode, showThemeDialog = false) }
    }

    // ── Tarif Varsayılanları ──────────────────────────────────────────────

    fun setDefaultCookingMethod(method: String) {
        updateAndSave { it.copy(defaultCookingMethod = method) }
    }

    fun toggleDefaultDiet(option: String) {
        updateAndSave { state ->
            val updated = if (option in state.defaultDietOptions)
                state.defaultDietOptions - option
            else
                state.defaultDietOptions + option
            state.copy(defaultDietOptions = updated)
        }
    }

    fun toggleDefaultGourmetMode() {
        updateAndSave { it.copy(defaultGourmetMode = !it.defaultGourmetMode) }
    }

    // ── Dialog ────────────────────────────────────────────────────────────

    fun showThemeDialog() = _uiState.update { it.copy(showThemeDialog = true) }
    fun dismissThemeDialog() = _uiState.update { it.copy(showThemeDialog = false) }
    fun showNotifyDaysDialog() = _uiState.update { it.copy(showNotifyDaysDialog = true) }
    fun dismissNotifyDaysDialog() = _uiState.update { it.copy(showNotifyDaysDialog = false) }
    fun showClearConfirmDialog() = _uiState.update { it.copy(showClearConfirmDialog = true) }
    fun dismissClearConfirmDialog() = _uiState.update { it.copy(showClearConfirmDialog = false) }

    // ── Veri ─────────────────────────────────────────────────────────────

    fun exportCsv() {
        viewModelScope.launch {
            val csv = useCases.exportInventory().first()
            _uiState.update { it.copy(exportCsvContent = csv) }
        }
    }

    fun onExportHandled() {
        _uiState.update { it.copy(exportCsvContent = null) }
    }

    fun clearAllInventory() {
        viewModelScope.launch {
            useCases.clearInventory()
            _uiState.update {
                it.copy(
                    showClearConfirmDialog = false,
                    snackbarMessage = "Tüm envanter silindi."
                )
            }
        }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // ── Yardımcı ──────────────────────────────────────────────────────────

    private fun updateAndSave(transform: (SettingsUiState) -> SettingsUiState) {
        _uiState.update(transform)
        viewModelScope.launch {
            useCases.saveSettings(_uiState.value.toAppSettings())
        }
    }
}
