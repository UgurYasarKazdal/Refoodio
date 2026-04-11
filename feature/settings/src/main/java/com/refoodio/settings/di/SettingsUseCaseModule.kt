package com.refoodio.settings.di

import com.refoodio.core.domain.repository.SettingsRepository
import com.refoodio.core.domain.use_case.settings.ClearInventoryUseCase
import com.refoodio.core.domain.use_case.settings.ExportInventoryUseCase
import com.refoodio.core.domain.use_case.settings.GetSettingsUseCase
import com.refoodio.core.domain.use_case.settings.SaveSettingsUseCase
import com.refoodio.core.domain.use_case.settings.SettingsUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object SettingsUseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideSettingsUseCases(repository: SettingsRepository): SettingsUseCases =
        SettingsUseCases(
            getSettings = GetSettingsUseCase(repository),
            saveSettings = SaveSettingsUseCase(repository),
            clearInventory = ClearInventoryUseCase(repository),
            exportInventory = ExportInventoryUseCase(repository)
        )
}
