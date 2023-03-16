package com.wittgroup.smartassistlib.datasources

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import com.wittgroup.smartassistlib.db.AppDatabase
import com.wittgroup.smartassistlib.db.entities.ConversationHistory

class ConversationHistoryDataSourceImpl(private val context: Context) : ConversationHistoryDataSource {

    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "smart_assist"
    ).build()

    private val dao = db.conversationHistoryDao()

    override suspend fun getConversationHistory(): List<ConversationHistory> = dao.getAll()

    override suspend fun getConversationById(id: Long): ConversationHistory = dao.loadByIds(id)

    @WorkerThread
    override suspend fun saveConversationHistory(conversationHistory: ConversationHistory) = dao.insertAll(conversationHistory)

    override suspend fun clearConversationHistory(conversationHistory: ConversationHistory) = dao.delete(conversationHistory)
}
