package com.gowittgroup.smartassistlib.data.datasources.ai

import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.ClarifyingQuestion
import com.gowittgroup.smartassistlib.domain.models.PromptAssembly
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.models.Template
import kotlinx.coroutines.flow.Flow

interface AiDataSource {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getReply(conversations: List<Conversation>): Resource<Flow<StreamResource<String>>>
    suspend fun fetchClarifyingQuestions(idea: String): Resource<Flow<List<ClarifyingQuestion>>>
    suspend fun fetchAssembledPrompt(templateId: String, details: Map<String, String>): Resource<Flow<PromptAssembly>>
    suspend fun fetchTemplates(): Resource<Flow<List<Template>>>
}