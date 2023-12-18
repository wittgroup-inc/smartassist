package com.gowittgroup.smartassistlib.datasources

import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Resource

interface AiToolsDataSource {
    suspend fun getAiTools(): Resource<List<AiTools>>
}