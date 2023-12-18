package com.gowittgroup.smartassistlib.datasources

import android.content.SharedPreferences
import com.gowittgroup.smartassistlib.Constants.CHAT_GPT_DEFAULT_CHAT_AI_MODEL
import com.gowittgroup.smartassistlib.Constants.GEMINI_DEFAULT_CHAT_AI_MODEL
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.aiModel
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.aiTool
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.readAloud
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.userId
import com.gowittgroup.smartassistlib.di.CHAT_GPT
import com.gowittgroup.smartassistlib.di.GEMINI
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.successOr
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import javax.inject.Inject
import javax.inject.Named

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
        var aiTool = AiTools.values()[pref.aiTool]
        if (aiTool == AiTools.NONE) {
            aiTool = AiTools.CHAT_GPT
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

    override suspend fun chooseAiModel(model: String) {
        mutex.withLock {
            pref.aiModel = model
        }
    }

    override suspend fun chooseAiTool(tool: AiTools) {
        mutex.withLock {
            pref.aiTool = tool.ordinal
        }
    }

    override suspend fun getUserId(): Resource<String> {
        var userId = pref.userId
        if (userId.isNullOrBlank()) {
            userId = UUID.randomUUID().toString()
            mutex.withLock {
                pref.userId = userId
            }
        }
        return Resource.Success(userId)
    }

    override suspend fun getDefaultChatModel(): String {
        return when (getSelectedAiTool().successOr(AiTools.CHAT_GPT)) {
            AiTools.NONE -> CHAT_GPT_DEFAULT_CHAT_AI_MODEL
            AiTools.CHAT_GPT -> CHAT_GPT_DEFAULT_CHAT_AI_MODEL
            AiTools.GEMINI -> GEMINI_DEFAULT_CHAT_AI_MODEL
        }
    }

}
