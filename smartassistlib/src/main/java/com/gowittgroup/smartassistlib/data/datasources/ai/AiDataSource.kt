package com.gowittgroup.smartassistlib.data.datasources.ai

import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import kotlinx.coroutines.flow.Flow

interface AiDataSource {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getReply(conversations: List<Conversation>): Resource<Flow<StreamResource<String>>>
}