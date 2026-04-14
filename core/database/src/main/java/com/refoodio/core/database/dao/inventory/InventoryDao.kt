package com.refoodio.core.database.dao.inventory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.refoodio.core.database.entity.inventory.InventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventoryItem: InventoryEntity): Long

    @Query("UPDATE inventory SET side_units = :sideUnits WHERE id = :id")
    suspend fun updateSideUnits(id: Int, sideUnits: String)

    @Update
    suspend fun updateInventory(inventoryItem: InventoryEntity)

    @Query("SELECT * FROM inventory")
    fun getInventoriesFlow(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE id = :id LIMIT 1")
    suspend fun getInventoryById(id: Int): InventoryEntity?

    @Delete
    suspend fun deleteInventory(inventoryItem: InventoryEntity)

    @Query("DELETE FROM inventory WHERE id IN (:idList)")
    suspend fun deleteItemsByIds(idList: List<Int>)

    @Query("DELETE FROM inventory")
    suspend fun deleteAllInventory()

    @Query("SELECT * FROM inventory")
    suspend fun getAllInventoriesOnce(): List<InventoryEntity>
}