package com.wittgroup.smartassistlib.datasources

import com.wittgroup.smartassistlib.models.Resource

interface AiDataSource {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getAnswer(query: String): Resource<String>
    suspend fun getReply(message: String): Resource<String>
}
