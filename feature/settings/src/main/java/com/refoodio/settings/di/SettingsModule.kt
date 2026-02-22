package com.refoodio.settings.di

import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.settings.navigation.SettingsNavImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsModule {
    @Binds
    @IntoSet
    abstract fun bindHomeNav(impl: SettingsNavImpl): FeatureNavEntry

}