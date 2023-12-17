package com.gowittgroup.smartassistlib.datasources

import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Resource
import javax.inject.Inject

class AiToolsDataSourceImpl @Inject constructor(): AiToolsDataSource {
    override suspend fun getAiTools(): Resource<List<AiTools>> {
        return Resource.Success(AiTools.entries)
    }
}