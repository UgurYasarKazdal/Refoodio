package com.refoodio.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListTypeConverter {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.Default.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { Json.Default.decodeFromString<List<String>>(it) }
    }
}