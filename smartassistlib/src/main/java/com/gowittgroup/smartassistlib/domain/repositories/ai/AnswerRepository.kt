package com.gowittgroup.smartassistlib.domain.repositories.ai

import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>>
}

