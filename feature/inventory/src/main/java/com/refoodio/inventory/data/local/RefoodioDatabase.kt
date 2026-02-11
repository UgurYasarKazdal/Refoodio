package com.refoodio.inventory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class], version = 1)
abstract class RefoodioDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}