package com.gowittgroup.smartassistlib.datasources

import com.gowittgroup.smartassistlib.models.Message
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow

interface AiDataSource {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getReply(message: List<Message>): Resource<Flow<StreamResource<String>>>
}
