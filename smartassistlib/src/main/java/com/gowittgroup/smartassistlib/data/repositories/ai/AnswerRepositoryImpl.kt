package com.gowittgroup.smartassistlib.data.repositories.ai

import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.ClarifyingQuestion
import com.gowittgroup.smartassistlib.domain.models.PromptAssembly
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.models.Template
import com.gowittgroup.smartassistlib.domain.repositories.ai.AnswerRepository
import com.gowittgroup.smartassistlib.util.AiDataSourceProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnswerRepositoryImpl @Inject constructor(
    private val dataSourceProvider: AiDataSourceProvider
) : AnswerRepository {
    override suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>> =
        dataSourceProvider.getDataSource().getReply(query)

    override suspend fun getClarifyingQuestions(roughIdea: String): Resource<Flow<List<ClarifyingQuestion>>> =
        dataSourceProvider.getDataSource().fetchClarifyingQuestions(roughIdea)

    override suspend fun assemblePrompt(templateId: String, details: Map<String, String>): Resource<Flow<PromptAssembly>> =
        dataSourceProvider.getDataSource().fetchAssembledPrompt(templateId, details)
    override suspend fun getTemplates(): Resource<Flow<List<Template>>> =
        dataSourceProvider.getDataSource().fetchTemplates()
}
