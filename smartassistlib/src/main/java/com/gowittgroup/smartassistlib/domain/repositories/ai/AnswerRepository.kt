package com.gowittgroup.smartassistlib.domain.repositories.ai

import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.ClarifyingQuestion
import com.gowittgroup.smartassistlib.domain.models.PromptAssembly
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.models.Template
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>>
    suspend fun getClarifyingQuestions(roughIdea: String): Resource<Flow<List<ClarifyingQuestion>>>
    suspend fun assemblePrompt(templateId: String, details: Map<String, String>): Resource<Flow<PromptAssembly>>
    suspend fun getTemplates(): Resource<Flow<List<Template>>>
}

