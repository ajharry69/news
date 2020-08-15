package com.xently.news.data.source.local

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.xently.common.utils.JSON_CONVERTER

object RoomTypeConverters {
    class StringListConverter {
        @TypeConverter
        fun stringListToJsonArray(strs: List<String>): String = JSON_CONVERTER.toJson(strs)

        @TypeConverter
        fun jsonArrayToString(jsonArray: String): List<String> =
            JSON_CONVERTER.fromJson(jsonArray, object : TypeToken<List<String>>() {}.type)
    }
}