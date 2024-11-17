package com.gowittgroup.smartassistlib.data.datasources.ai

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.models.ai.Message
import kotlinx.coroutines.flow.Flow

interface AiDataSource {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getReply(message: List<Message>): Resource<Flow<StreamResource<String>>>
}