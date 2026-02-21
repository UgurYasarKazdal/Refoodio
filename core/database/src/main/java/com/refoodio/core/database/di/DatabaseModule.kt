package com.refoodio.core.database.di

import android.content.Context
import androidx.room.Room
import com.refoodio.core.database.RefoodioDatabase
import com.refoodio.core.database.dao.catalog.FoodCatalogDao
import com.refoodio.core.database.dao.catalog.FoodSuggestionDao
import com.refoodio.core.database.dao.inventory.InventoryDao
import com.refoodio.core.database.migrations.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RefoodioDatabase {
        return Room.databaseBuilder(
            context, RefoodioDatabase::class.java, "refoodio_db"
        ).addMigrations(MIGRATION_2_3)
            //.fallbackToDestructiveMigration(true)//migration kullanma tüm tabloları sil
            .build()
    }

    @Provides
    fun provideInventoryDao(db: RefoodioDatabase): InventoryDao = db.inventoryDao()

    @Provides
    fun provideFoodCatalogDao(database: RefoodioDatabase): FoodCatalogDao {
        return database.foodCatalogDao()
    }

    @Provides
    fun provideFoodSuggestionDao(database: RefoodioDatabase): FoodSuggestionDao {
        return database.foodSuggestionDao()
    }
}