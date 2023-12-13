package com.gowittgroup.smartassistlib.datasources

import android.content.SharedPreferences
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.aiModel
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.readAloud
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.userId
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

private const val CHAT_DEFAULT_AI_MODEL = "gpt-3.5-turbo"

class SettingsDataSourceImpl(private val pref: SharedPreferences) : SettingsDataSource {
    private val mutex = Mutex()

    override suspend fun getSelectedAiModel(): Resource<String> {
        var aiModel = pref.aiModel
        if (aiModel.isNullOrBlank()) {
            aiModel = CHAT_DEFAULT_AI_MODEL
            mutex.withLock {
                pref.aiModel = aiModel
            }
        }
        return Resource.Success(aiModel)
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
}
