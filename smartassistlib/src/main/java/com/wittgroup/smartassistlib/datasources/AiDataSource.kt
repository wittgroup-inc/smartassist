package com.wittgroup.smartassistlib.datasources

import com.wittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

interface AiDataSource {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getAnswer(query: String): Resource<Flow<String>>
}
