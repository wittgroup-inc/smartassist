package com.gowittgroup.smartassistlib.data.datasources.conversationhistory

import androidx.annotation.WorkerThread
import com.gowittgroup.smartassistlib.db.dao.ConversationHistoryDao
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversationHistoryDataSourceImpl @Inject constructor(private val dao: ConversationHistoryDao) :
    ConversationHistoryDataSource {

    override suspend fun getConversationHistory(): Flow<List<ConversationHistory>> = dao.getAll()

    override suspend fun getConversationById(id: Long): ConversationHistory = dao.loadByIds(id)

    @WorkerThread
    override suspend fun saveConversationHistory(conversationHistory: ConversationHistory) = dao.insertAll(conversationHistory)

    override suspend fun clearConversationHistory(conversationHistory: ConversationHistory) = dao.delete(conversationHistory)
}
