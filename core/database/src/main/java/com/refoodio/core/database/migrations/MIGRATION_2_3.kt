package com.refoodio.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.recipe.FoodCategory

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN unit_id INTEGER NOT NULL DEFAULT ${FoodUnit.KILOGRAM.id}")
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN category_id INTEGER NOT NULL DEFAULT ${FoodCategory.OTHER.id}")
    }
}