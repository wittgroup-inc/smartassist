package com.gowittgroup.smartassistlib.data.repositories.prompts

import com.gowittgroup.smartassistlib.data.datasources.prompts.PromptsDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.prompts.PromptsRepository
import com.gowittgroup.smartassistlib.models.prompts.Prompts
import com.gowittgroup.smartassistlib.models.prompts.PromptsCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PromptsRepositoryImpl @Inject constructor(private val promptsDataSource: PromptsDataSource):
    PromptsRepository {
    override suspend fun getPromptsCategories(): Resource<Flow<List<PromptsCategory>>> = promptsDataSource.getPromptsCategories()
    override suspend fun getPromptsForCategory(categoryId: Long): Resource<Flow<Prompts>> = promptsDataSource.getPromptsForCategory(categoryId)
    override suspend fun getAllPrompts(): Resource<Flow<List<Prompts>>> = promptsDataSource.getAllPrompts()
}
