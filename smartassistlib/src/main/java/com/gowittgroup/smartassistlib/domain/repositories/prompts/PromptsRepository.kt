package com.gowittgroup.smartassistlib.domain.repositories.prompts

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.prompts.Prompts
import com.gowittgroup.smartassistlib.models.prompts.PromptsCategory
import kotlinx.coroutines.flow.Flow

interface PromptsRepository {
    suspend fun getPromptsCategories(): Resource<Flow<List<PromptsCategory>>>
    suspend fun getPromptsForCategory(categoryId: Long): Resource<Flow<Prompts>>
    suspend fun getAllPrompts(): Resource<Flow<List<Prompts>>>
}
