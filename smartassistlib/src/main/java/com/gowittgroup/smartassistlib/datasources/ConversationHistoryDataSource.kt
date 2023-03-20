package com.gowittgroup.smartassistlib.datasources

import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import kotlinx.coroutines.flow.Flow

interface ConversationHistoryDataSource {
    suspend fun getConversationHistory(): Flow<List<ConversationHistory>>
    suspend fun getConversationById(id: Long): ConversationHistory
    suspend fun saveConversationHistory(conversationHistory: ConversationHistory)
    suspend fun clearConversationHistory(conversationHistory: ConversationHistory)
}
