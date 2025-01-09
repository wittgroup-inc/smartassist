package com.gowittgroup.smartassistlib.util

import com.google.firebase.Firebase
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.HarmBlockThreshold
import com.google.firebase.vertexai.type.HarmCategory
import com.google.firebase.vertexai.type.SafetySetting
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
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

        // Retrieve the selected AI model name, fallback to default if empty
        val model = settingsDataSource.getSelectedAiModel().successOr("")
        val finalModelName = model.ifEmpty { settingsDataSource.getDefaultChatModel() }

        return Firebase.vertexAI.generativeModel(
            modelName = this.modelName.ifEmpty { finalModelName },
            generationConfig = config,
            safetySettings = listOf(harassmentSafety, hateSpeechSafety)
        )
    }
}