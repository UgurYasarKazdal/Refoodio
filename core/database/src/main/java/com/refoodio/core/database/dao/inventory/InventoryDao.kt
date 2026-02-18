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
    suspend fun insertProduct(product: InventoryEntity)

    @Query("SELECT * FROM products")
    fun getProductsFlow(): Flow<List<InventoryEntity>>

    @Delete
    suspend fun deleteProduct(product: InventoryEntity)
}