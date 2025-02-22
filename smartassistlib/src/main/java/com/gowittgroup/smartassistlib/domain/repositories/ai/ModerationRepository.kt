package com.gowittgroup.smartassistlib.domain.repositories.ai

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.ModerationResult

interface ModerationRepository {
    suspend fun isContentSafe(input: String): Resource<ModerationResult>
}

