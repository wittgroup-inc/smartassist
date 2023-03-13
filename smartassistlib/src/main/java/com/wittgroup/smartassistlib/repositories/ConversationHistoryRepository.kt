package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.db.entities.ConversationHistory
import com.wittgroup.smartassistlib.models.Resource

interface ConversationHistoryRepository {
    suspend fun getConversationHistory(): Resource<List<ConversationHistory>>
    suspend fun getConversationById(id: Long): Resource<ConversationHistory>
    suspend fun saveConversationHistory(conversationHistory: ConversationHistory)
    suspend fun clearConversationHistory(conversationHistory: ConversationHistory)
}
