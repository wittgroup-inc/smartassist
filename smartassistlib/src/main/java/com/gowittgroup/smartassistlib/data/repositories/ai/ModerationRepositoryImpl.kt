package com.gowittgroup.smartassistlib.data.repositories.ai

import com.gowittgroup.smartassistlib.data.datasources.ai.ModerationDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.ai.ModerationRepository
import com.gowittgroup.smartassistlib.models.ai.ModerationResult
import javax.inject.Inject

class ModerationRepositoryImpl @Inject constructor(private val dataSource: ModerationDataSource) :
    ModerationRepository {
    override suspend fun isContentSafe(input: String): Resource<ModerationResult> =
        dataSource.getModerationResult(input)
}

