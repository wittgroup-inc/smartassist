package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow

class AnswerRepositoryImpl(private val aiDataSource: AiDataSource) : AnswerRepository {
    override suspend fun getAnswer(query: String): Resource<Flow<StreamResource<String>>> = aiDataSource.getAnswer(query)
    override suspend fun getReply(query: String): Resource<Flow<StreamResource<String>>> = aiDataSource.getReply(query)
}
