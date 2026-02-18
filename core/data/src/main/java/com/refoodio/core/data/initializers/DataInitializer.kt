package com.refoodio.core.data.initializers

import android.content.Context
import androidx.startup.Initializer
import com.refoodio.core.data.di.ApplicationScope
import com.refoodio.core.domain.repository.FoodCatalogRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DataInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            DataInitializerEntryPoint::class.java
        )

        val repository = entryPoint.foodCatalogRepository()
        val scope = entryPoint.applicationScope()

        scope.launch {
            repository.loadFoodCatalog()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DataInitializerEntryPoint {
        fun foodCatalogRepository(): FoodCatalogRepository

        @ApplicationScope
        fun applicationScope(): CoroutineScope
    }
}