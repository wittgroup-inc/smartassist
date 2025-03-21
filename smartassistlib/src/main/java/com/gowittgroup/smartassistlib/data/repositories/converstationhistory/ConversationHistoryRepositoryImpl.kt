package com.gowittgroup.smartassistlib.data.repositories.converstationhistory

import com.gowittgroup.smartassistlib.data.datasources.conversationhistory.ConversationHistoryDataSource
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.converstationhistory.ConversationHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversationHistoryRepositoryImpl @Inject constructor(private val conversationHistoryDataSource: ConversationHistoryDataSource) :
    ConversationHistoryRepository {

    override suspend fun getConversationHistory(): Resource<Flow<List<ConversationHistory>>> =
        Resource.Success(conversationHistoryDataSource.getConversationHistory())

    override suspend fun getConversationById(id: Long): Resource<ConversationHistory> =
        Resource.Success(conversationHistoryDataSource.getConversationById(id))

    override suspend fun saveConversationHistory(conversationHistory: ConversationHistory) =
        conversationHistoryDataSource.saveConversationHistory(conversationHistory)

    override suspend fun clearConversationHistory(conversationHistory: ConversationHistory) =
        conversationHistoryDataSource.clearConversationHistory(conversationHistory)
}
