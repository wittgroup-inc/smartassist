package com.gowittgroup.smartassistlib.data.datasources.settings

import android.content.SharedPreferences
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.sharedpref.LocalPreferenceManager.aiModel
import com.gowittgroup.smartassistlib.sharedpref.LocalPreferenceManager.aiTool
import com.gowittgroup.smartassistlib.sharedpref.LocalPreferenceManager.handsFreeMode
import com.gowittgroup.smartassistlib.sharedpref.LocalPreferenceManager.readAloud
import com.gowittgroup.smartassistlib.sharedpref.LocalPreferenceManager.userId
import com.gowittgroup.smartassistlib.sharedpref.LocalPreferenceManager.userSubscriptionStatus
import com.gowittgroup.smartassistlib.util.Constants.CHAT_GPT_DEFAULT_CHAT_AI_MODEL
import com.gowittgroup.smartassistlib.util.Constants.DEFAULT_AI_TOOL
import com.gowittgroup.smartassistlib.util.Constants.GEMINI_DEFAULT_CHAT_AI_MODEL
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class SettingsDataSourceImpl @Inject constructor(
    private val pref: SharedPreferences,
) : SettingsDataSource {
    private val mutex = Mutex()

    override suspend fun getSelectedAiModel(): Resource<String> {
        var aiModel = pref.aiModel
        if (aiModel.isNullOrBlank()) {
            aiModel = getDefaultChatModel()
            mutex.withLock {
                pref.aiModel = aiModel
            }
        }
        return Resource.Success(aiModel)
    }

    override suspend fun getSelectedAiTool(): Resource<AiTools> {
        var aiTool = AiTools.entries[pref.aiTool]
        if (aiTool == AiTools.NONE) {
            aiTool = DEFAULT_AI_TOOL
            mutex.withLock {
                pref.aiTool = aiTool.ordinal
            }
        }
        return Resource.Success(aiTool)
    }


    override suspend fun getReadAloud(): Resource<Boolean> = Resource.Success(pref.readAloud)

    override suspend fun toggleReadAloud(isOn: Boolean) {
        mutex.withLock {
            pref.readAloud = isOn
        }
    }

    override suspend fun chooseAiModel(chatModel: String) {
        mutex.withLock {
            pref.aiModel = chatModel
        }
    }

    override suspend fun chooseAiTool(tool: AiTools) {
        mutex.withLock {
            pref.aiTool = tool.ordinal
        }
    }

    override suspend fun getUserId(): Resource<String> {
        val userId = pref.userId
        if (userId.isNullOrBlank()) {
            return Resource.Error(RuntimeException("User not found."))
        }
        return Resource.Success(userId)
    }

    override suspend fun setUserId(userId: String?) {
        mutex.withLock {
            pref.userId = userId
        }
    }

    override suspend fun getDefaultChatModel(): String {
        return when (getSelectedAiTool().successOr(DEFAULT_AI_TOOL)) {
            AiTools.NONE -> CHAT_GPT_DEFAULT_CHAT_AI_MODEL
            AiTools.CHAT_GPT -> CHAT_GPT_DEFAULT_CHAT_AI_MODEL
            AiTools.GEMINI -> GEMINI_DEFAULT_CHAT_AI_MODEL
        }
    }

    override suspend fun toggleHandsFreeMode(isOn: Boolean) {
        mutex.withLock {
            pref.handsFreeMode = isOn
        }
    }

    override suspend fun getHandsFreeMode(): Resource<Boolean> =
        Resource.Success(pref.handsFreeMode)

    override suspend fun setUserSubscriptionStatus(active: Boolean) {
        mutex.withLock {
            pref.userSubscriptionStatus = active
        }
    }

    override suspend fun getUserSubscriptionStatus(): Resource<Boolean> =
        Resource.Success(pref.userSubscriptionStatus)

}
