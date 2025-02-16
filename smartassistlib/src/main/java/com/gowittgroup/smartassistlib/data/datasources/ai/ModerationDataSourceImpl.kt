package com.gowittgroup.smartassistlib.data.datasources.ai

import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.Categories
import com.gowittgroup.smartassistlib.models.ai.ModerationRequest
import com.gowittgroup.smartassistlib.models.ai.ModerationResult
import com.gowittgroup.smartassistlib.network.ChatGptService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ModerationDataSourceImpl @Inject constructor(private val chatGptService: ChatGptService) :
    ModerationDataSource {
    override suspend fun getModerationResult(input: String): Resource<ModerationResult> =
        withContext(Dispatchers.IO) {

            try {
                val res =
                    chatGptService.moderation(ModerationRequest(input = input)).results.firstOrNull()

                res?.let {
                    return@withContext Resource.Success(
                        ModerationResult(
                            isSafe = !it.flagged,
                            cause = getTrueCategories(it.categories)
                        )
                    )
                }
            } catch (e: Exception) {
                SmartLog.e("ModerationDataSourceImpl", "Moderation api failed ${e.message}")
            }

            return@withContext Resource.Error(RuntimeException("Failed to get moderation Result"))
        }

    private fun getTrueCategories(categories: Categories): List<String> {
        return listOf(
            "sexual" to categories.sexual,
            "sexual/minors" to categories.sexualminors,
            "harassment" to categories.harassment,
            "harassment/threatening" to categories.harassmentthreatening,
            "hate" to categories.hate,
            "hate/threatening" to categories.hatethreatening,
            "illicit" to categories.illicit,
            "illicit/violent" to categories.illicitviolent,
            "self-harm" to categories.selfHarm,
            "self-harm/intent" to categories.selfHarmintent,
            "self-harm/instructions" to categories.selfHarminstructions,
            "violence" to categories.violence,
            "violence/graphic" to categories.violencegraphic
        ).filter { it.second }
            .map { it.first }
    }
}