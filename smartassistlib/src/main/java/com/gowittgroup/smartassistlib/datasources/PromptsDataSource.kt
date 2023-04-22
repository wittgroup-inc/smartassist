package com.gowittgroup.smartassistlib.datasources

import com.gowittgroup.smartassistlib.models.Prompts
import com.gowittgroup.smartassistlib.models.PromptsCategory
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.flow.Flow

interface PromptsDataSource {
   suspend fun getPromptsCategories(): Resource<Flow<List<PromptsCategory>>>
   suspend fun getPromptsForCategory(categoryId: Long): Resource<Flow<Prompts>>
   suspend fun getAllPrompts(): Resource<Flow<List<Prompts>>>
}
