package com.refoodio.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.recipe.FoodCategory

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE inventory ADD COLUMN unit_id INTEGER NOT NULL DEFAULT ${FoodUnit.KILOGRAM.id}")
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE inventory ADD COLUMN category_id INTEGER NOT NULL DEFAULT ${FoodCategory.OTHER.id}")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE inventory ADD COLUMN price REAL")
        db.execSQL("ALTER TABLE inventory ADD COLUMN store_name TEXT")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS waste_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                quantity REAL NOT NULL,
                unitId INTEGER NOT NULL,
                wastedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}