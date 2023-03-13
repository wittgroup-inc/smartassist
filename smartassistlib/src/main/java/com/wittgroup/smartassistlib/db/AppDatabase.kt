package com.wittgroup.smartassistlib.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wittgroup.smartassistlib.db.dao.ConversationHistoryDao
import com.wittgroup.smartassistlib.db.entities.Conversation
import com.wittgroup.smartassistlib.db.entities.ConversationHistory

@Database(entities = [ConversationHistory::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationHistoryDao(): ConversationHistoryDao
}
