package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>>
}

