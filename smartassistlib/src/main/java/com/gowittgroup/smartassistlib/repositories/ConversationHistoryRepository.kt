package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

interface ConversationHistoryRepository {
    suspend fun getConversationHistory(): Resource<Flow<List<ConversationHistory>>>
    suspend fun getConversationById(id: Long): Resource<ConversationHistory>
    suspend fun saveConversationHistory(conversationHistory: ConversationHistory)
    suspend fun clearConversationHistory(conversationHistory: ConversationHistory)
}
