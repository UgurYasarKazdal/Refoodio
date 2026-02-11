package com.refoodio.inventory.di

import android.content.Context
import androidx.room.Room
import com.refoodio.inventory.data.local.ProductDao
import com.refoodio.inventory.data.local.RefoodioDatabase
import com.refoodio.inventory.data.repository.InventoryRepositoryImpl
import com.refoodio.inventory.domain.repository.InventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Uygulama boyunca tek bir instance olsun
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RefoodioDatabase {
        return Room.databaseBuilder(
            context,
            RefoodioDatabase::class.java,
            "refoodio_db"
        ).build()
    }

    @Provides
    fun provideProductDao(database: RefoodioDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(dao: ProductDao): InventoryRepository {
        return InventoryRepositoryImpl(dao) // Implementation'ı interface'e bağlıyoruz
    }
}