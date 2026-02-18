package com.refoodio.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val jsonConfig = Json { ignoreUnknownKeys = true }

class StringListTypeConverter {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { jsonConfig.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { jsonConfig.decodeFromString<List<String>>(it) }
    }
}