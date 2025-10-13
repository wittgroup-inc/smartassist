package com.gowittgroup.smartassistlib.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.gowittgroup.smartassistlib.db.entities.Conversation
import java.util.*

class Converters {

    @TypeConverter
    fun listToJson(value: List<Conversation>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Conversation>::class.java).toList()

    @TypeConverter
    fun toDate(dateLong: Long): Date {
        return Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }
}
