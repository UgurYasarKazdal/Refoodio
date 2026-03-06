package com.refoodio.core.database.dao.inventory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.refoodio.core.database.entity.inventory.InventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventoryItem: InventoryEntity)

    @Query("SELECT * FROM inventory")
    fun getInventoriesFlow(): Flow<List<InventoryEntity>>

    @Delete
    suspend fun deleteInventory(inventoryItem: InventoryEntity)

    @Query("DELETE FROM inventory WHERE id IN (:idList)")
    suspend fun deleteItemsByIds(idList: List<Int>)
}