package com.gowittgroup.smartassistlib.data.datasources.ai

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.AiTools
import javax.inject.Inject

class AiToolsDataSourceImpl @Inject constructor(): AiToolsDataSource {
    override suspend fun getAiTools(): Resource<List<AiTools>> {
        return Resource.Success(AiTools.entries)
    }
}