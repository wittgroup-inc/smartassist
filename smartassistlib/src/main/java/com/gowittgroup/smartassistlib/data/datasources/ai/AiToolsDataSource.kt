package com.gowittgroup.smartassistlib.data.datasources.ai

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.AiTools

interface AiToolsDataSource {
    suspend fun getAiTools(): Resource<List<AiTools>>
}