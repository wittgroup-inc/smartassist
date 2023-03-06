package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    suspend fun getAnswer(query: String): Resource<Flow<String>>
}

