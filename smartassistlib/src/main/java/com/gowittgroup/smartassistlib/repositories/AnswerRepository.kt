package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    suspend fun getAnswer(query: String): Resource<Flow<StreamResource<String>>>
    suspend fun getReply(query: String): Resource<Flow<StreamResource<String>>>
}

