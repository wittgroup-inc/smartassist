package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.datasources.ConversationHistoryDataSource
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

class ConversationHistoryRepositoryImpl(private val conversationHistoryDataSource: ConversationHistoryDataSource) : ConversationHistoryRepository {

    override suspend fun getConversationHistory(): Resource<Flow<List<ConversationHistory>>> =
        Resource.Success(conversationHistoryDataSource.getConversationHistory())

    override suspend fun getConversationById(id: Long): Resource<ConversationHistory> =
        Resource.Success(conversationHistoryDataSource.getConversationById(id))

    override suspend fun saveConversationHistory(conversationHistory: ConversationHistory) =
        conversationHistoryDataSource.saveConversationHistory(conversationHistory)

    override suspend fun clearConversationHistory(conversationHistory: ConversationHistory) =
        conversationHistoryDataSource.clearConversationHistory(conversationHistory)
}
