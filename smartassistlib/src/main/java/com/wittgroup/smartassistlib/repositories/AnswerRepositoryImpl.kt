package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.datasources.AiDataSource
import com.wittgroup.smartassistlib.models.Resource
import com.wittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow

class AnswerRepositoryImpl(private val aiDataSource: AiDataSource) : AnswerRepository {
    override suspend fun getAnswer(query: String): Resource<Flow<StreamResource<String>>> = aiDataSource.getAnswer(query)
    override suspend fun getReply(query: String): Resource<Flow<StreamResource<String>>> = aiDataSource.getReply(query)
}
