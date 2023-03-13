package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.db.entities.ConversationHistory

interface ConversationHistoryRepository {
    suspend fun getConversationHistory(): List<ConversationHistory>
    suspend fun getConversationById(id: Long): ConversationHistory
    suspend fun saveConversationHistory(conversationHistory: ConversationHistory)
    suspend fun clearConversationHistory(conversationHistory: ConversationHistory)
}
