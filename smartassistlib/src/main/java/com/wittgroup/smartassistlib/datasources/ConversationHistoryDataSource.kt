package com.wittgroup.smartassistlib.datasources

import com.wittgroup.smartassistlib.db.entities.ConversationHistory

interface ConversationHistoryDataSource {
    suspend fun getConversationHistory(): List<ConversationHistory>
    suspend fun getConversationById(id: Long): ConversationHistory
    suspend fun saveConversationHistory(conversationHistory: ConversationHistory)
    suspend fun clearConversationHistory(conversationHistory: ConversationHistory)
}
