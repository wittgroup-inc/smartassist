package com.wittgroup.smartassistlib.db

import android.content.Context
import androidx.room.Room

class DbHelper {
    fun getRoomDb(context: Context){
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).build()
    }
}
