package com.gowittgroup.smartassistlib.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gowittgroup.smartassistlib.db.dao.ConversationHistoryDao
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory

@Database(entities = [ConversationHistory::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationHistoryDao(): ConversationHistoryDao
}
