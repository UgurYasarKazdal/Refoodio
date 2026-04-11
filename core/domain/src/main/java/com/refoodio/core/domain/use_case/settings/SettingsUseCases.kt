package com.refoodio.core.domain.use_case.settings

data class SettingsUseCases(
    val getSettings: GetSettingsUseCase,
    val saveSettings: SaveSettingsUseCase,
    val clearInventory: ClearInventoryUseCase,
    val exportInventory: ExportInventoryUseCase
)
