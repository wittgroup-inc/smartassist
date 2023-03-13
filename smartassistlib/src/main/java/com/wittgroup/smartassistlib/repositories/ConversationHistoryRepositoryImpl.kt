package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.datasources.ConversationHistoryDataSource
import com.wittgroup.smartassistlib.db.entities.ConversationHistory

class ConversationHistoryRepositoryImpl(private val conversationHistoryDataSource: ConversationHistoryDataSource): ConversationHistoryRepository {

    override suspend fun getConversationHistory(): List<ConversationHistory>  = conversationHistoryDataSource.getConversationHistory()

    override suspend fun getConversationById(id: Long): ConversationHistory = conversationHistoryDataSource.getConversationById(id)

    override suspend fun saveConversationHistory(conversationHistory: ConversationHistory) = conversationHistoryDataSource.saveConversationHistory(conversationHistory)

    override suspend fun clearConversationHistory(conversationHistory: ConversationHistory) = conversationHistoryDataSource.saveConversationHistory(conversationHistory)
}
