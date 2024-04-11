package com.gowittgroup.smartassistlib.datasources


import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.options
import com.gowittgroup.smartassistlib.models.AiTools

import com.gowittgroup.smartassistlib.models.Message
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import com.gowittgroup.smartassistlib.models.successOr
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class Gemini @Inject constructor(private val settingsDataSource: SettingsDataSource, @ApplicationContext private val context: Context) : AiDataSource {


    override suspend fun getModels(): Resource<List<String>> {
        return Resource.Success(listOf(settingsDataSource.getDefaultChatModel()))
    }

    override suspend fun getReply(message: List<Message>): Resource<Flow<StreamResource<String>>> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "You will get reply from : Gemini")
            val generativeModel = configureGenerativeModel()
            val history = message
                .filterIndexed { index, _ -> index != message.size - 1 }
                .filter { it.role != Message.ROLE_SYSTEM }
                .map {
                    content(role = if (it.role == Message.ROLE_ASSISTANT) Message.ROLE_MODEL else Message.ROLE_USER) {
                        text(
                            it.content!!
                        )
                    }
                }

            val result = sendRequest(generativeModel, history, message)
            delay(500)
            Resource.Success(result)
        }

    private fun sendRequest(
        generativeModel: GenerativeModel,
        history: List<Content>,
        message: List<Message>
    ): Flow<StreamResource<String>> {
        val chat = generativeModel.startChat(
            history = history
        )
        var isStart = true
        val result = flow {

            val response = chat.sendMessageStream(message.last().content!!)
            response.collect {
                if (isStart) {
                    emit(StreamResource.Initiated(AiTools.GEMINI))
                    emit(StreamResource.StreamStarted(""))
                    isStart = false
                }
                Log.d(TAG, "In-progress: ${it.text}")

                it.text?.let {
                    text ->
                    val tokens = text.split(" ")
                    tokens.forEachIndexed { index, token ->
                        val modifiedToken = if (index != tokens.size - 1) "$token " else token
                        delay(10)
                        emit(StreamResource.StreamInProgress(modifiedToken))
                    }
                }
            }
            Log.d(TAG, "Completed.")
            emit(StreamResource.StreamCompleted(true))

        }.catch { e ->
            Log.e(TAG, e.stackTraceToString())
            emit(StreamResource.Error(RuntimeException(e.message)))
            Resource.Error(RuntimeException(e.message))
        }
        return result
    }

    private suspend fun configureGenerativeModel(): GenerativeModel {
        val config = generationConfig {
            temperature = 0.9f
            topK = 16
            topP = 0.1f
            maxOutputTokens = 2048
        }

        val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)

        val hateSpeechSafety =
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH)

        val model = settingsDataSource.getSelectedAiModel().successOr("")
        return GenerativeModel(
            modelName = model.ifEmpty { settingsDataSource.getDefaultChatModel() },
            apiKey =  Firebase.options.apiKey,
            generationConfig = config,
            safetySettings = listOf(
                harassmentSafety, hateSpeechSafety
            )
        )
    }

    companion object {
        private val TAG = Gemini::class.simpleName
    }
}