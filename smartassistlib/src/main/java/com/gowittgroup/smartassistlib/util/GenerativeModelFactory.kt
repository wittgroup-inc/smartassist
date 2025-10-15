package com.gowittgroup.smartassistlib.util

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.generationConfig
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.domain.models.successOr
import javax.inject.Inject

class GenerativeModelFactory @Inject constructor(
    private val settingsDataSource: SettingsDataSource
) {

    private var modelName: String = ""
    private var apiKey: String = ""

    fun setModel(modelName: String) {
        this.modelName = modelName
    }

    fun setApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    suspend fun createGenerativeModel(): GenerativeModel {
        val config = generationConfig {
            temperature = 0.9f
            topK = 16
            topP = 0.1f
            maxOutputTokens = 2048
        }

        val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.ONLY_HIGH)
        val hateSpeechSafety = SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.ONLY_HIGH)

        val model = settingsDataSource.getSelectedAiModel().successOr("")
        val finalModelName = model.ifEmpty { settingsDataSource.getDefaultChatModel() }

        return Firebase.ai.generativeModel(
            modelName = finalModelName,
            generationConfig = config,
            safetySettings = listOf(harassmentSafety, hateSpeechSafety)
        )
    }
}