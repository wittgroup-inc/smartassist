package com.wittgroup.smartassistlib.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.wittgroup.smartassistlib.db.entities.Conversation

class Converters {

    @TypeConverter
    fun listToJson(value: List<Conversation>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Conversation>::class.java).toList()
}
