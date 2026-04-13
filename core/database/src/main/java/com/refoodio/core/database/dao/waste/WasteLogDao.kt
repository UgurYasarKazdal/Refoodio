package com.refoodio.core.database.dao.waste

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.refoodio.core.database.entity.waste.WasteLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WasteLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WasteLogEntity)

    @Query("SELECT * FROM waste_log ORDER BY wastedAt DESC")
    fun getAll(): Flow<List<WasteLogEntity>>

    @Query("SELECT COUNT(*) FROM waste_log")
    suspend fun count(): Int
}
