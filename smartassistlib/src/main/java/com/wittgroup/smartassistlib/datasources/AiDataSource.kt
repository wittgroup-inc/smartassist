package com.wittgroup.smartassistlib.datasources

import com.wittgroup.smartassistlib.models.Resource
import com.wittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow

interface AiDataSource {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getAnswer(query: String): Resource<Flow<StreamResource<String>>>
    suspend fun getReply(message: String): Resource<Flow<StreamResource<String>>>
}
