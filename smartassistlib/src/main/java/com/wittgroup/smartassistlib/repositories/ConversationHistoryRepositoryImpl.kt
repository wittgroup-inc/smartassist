package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.datasources.ConversationHistoryDataSource
import com.wittgroup.smartassistlib.db.entities.ConversationHistory
import com.wittgroup.smartassistlib.models.Resource

class ConversationHistoryRepositoryImpl(private val conversationHistoryDataSource: ConversationHistoryDataSource) : ConversationHistoryRepository {

    override suspend fun getConversationHistory(): Resource<List<ConversationHistory>> =
        Resource.Success(conversationHistoryDataSource.getConversationHistory())

    override suspend fun getConversationById(id: Long): Resource<ConversationHistory> =
        Resource.Success(conversationHistoryDataSource.getConversationById(id))

    override suspend fun saveConversationHistory(conversationHistory: ConversationHistory) =
        conversationHistoryDataSource.saveConversationHistory(conversationHistory)

    override suspend fun clearConversationHistory(conversationHistory: ConversationHistory) =
        conversationHistoryDataSource.saveConversationHistory(conversationHistory)
}
