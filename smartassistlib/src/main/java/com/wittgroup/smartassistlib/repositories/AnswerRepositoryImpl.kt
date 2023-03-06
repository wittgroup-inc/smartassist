package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.datasources.AiDataSource
import com.wittgroup.smartassistlib.models.Resource

class AnswerRepositoryImpl(private val aiDataSource: AiDataSource) : AnswerRepository {
    override suspend fun getAnswer(query: String): Resource<String> = aiDataSource.getReply(query)
}
