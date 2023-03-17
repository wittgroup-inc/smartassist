package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.models.Resource
import com.wittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    suspend fun getAnswer(query: String): Resource<Flow<StreamResource<String>>>
    suspend fun getReply(query: String): Resource<Flow<StreamResource<String>>>
}

