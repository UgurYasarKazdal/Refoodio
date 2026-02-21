package com.refoodio.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.refoodio.core.domain.model.inventory.FoodUnit

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN unit_id INTEGER NOT NULL DEFAULT ${FoodUnit.KILOGRAM.id}")
    }
}