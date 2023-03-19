package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.db.entities.ConversationHistory
import com.wittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

interface ConversationHistoryRepository {
    suspend fun getConversationHistory(): Resource<Flow<List<ConversationHistory>>>
    suspend fun getConversationById(id: Long): Resource<ConversationHistory>
    suspend fun saveConversationHistory(conversationHistory: ConversationHistory)
    suspend fun clearConversationHistory(conversationHistory: ConversationHistory)
}
