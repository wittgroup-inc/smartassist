package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.datasources.PromptsDataSource
import com.gowittgroup.smartassistlib.models.Prompts
import com.gowittgroup.smartassistlib.models.PromptsCategory
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

class PromptsRepositoryImpl(private val promptsDataSource: PromptsDataSource): PromptsRepository {
    override suspend fun getPromptsCategories(): Resource<Flow<List<PromptsCategory>>> = promptsDataSource.getPromptsCategories()
    override suspend fun getPromptsForCategory(categoryId: Long): Resource<Flow<Prompts>> = promptsDataSource.getPromptsForCategory(categoryId)
    override suspend fun getAllPrompts(): Resource<Flow<List<Prompts>>> = promptsDataSource.getAllPrompts()
}
