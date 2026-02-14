package com.refoodio.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.refoodio.core.database.dao.inventory.InventoryDao
import com.refoodio.core.database.entity.inventory.InventoryEntity

@Database(entities = [InventoryEntity::class], version = 1)
abstract class RefoodioDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
}