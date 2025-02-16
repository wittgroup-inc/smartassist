package com.gowittgroup.smartassistlib.data.datasources.ai

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.ModerationResult

interface ModerationDataSource {
    suspend fun getModerationResult(input: String): Resource<ModerationResult>
}