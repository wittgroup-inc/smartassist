package com.gowittgroup.smartassistlib.data.datasources.ai


import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.Content
import com.google.firebase.vertexai.type.content
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.mappers.toMessages
import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.models.ai.Message
import com.gowittgroup.smartassistlib.util.GenerativeModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class Gemini @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val generativeModelFactory: GenerativeModelFactory
) : AiDataSource {

    override suspend fun getModels(): Resource<List<String>> {
        return Resource.Success(listOf(settingsDataSource.getDefaultChatModel()))
    }

    override suspend fun getReply(conversations: List<Conversation>): Resource<Flow<StreamResource<String>>> =
        withContext(Dispatchers.IO) {
            SmartLog.d(TAG, "You will get reply from : Gemini")
            val generativeModel = generativeModelFactory.createGenerativeModel()
            val messages = conversations.toMessages()
            val history = messages
                .filterIndexed { index, _ -> index != messages.size - 1 }
                .filter { it.role != Message.ROLE_SYSTEM }
                .filter { !it.content.isNullOrBlank() }
                .map {
                    content(role = if (it.role == Message.ROLE_ASSISTANT) Message.ROLE_MODEL else Message.ROLE_USER) {
                        text(
                            it.content!!
                        )
                    }
                }

            val result = sendRequest(generativeModel, history, messages.last().content?:"")
            delay(500)
            Resource.Success(result)
        }

    private fun sendRequest(
        generativeModel: GenerativeModel,
        history: List<Content>,
        prompt: String
    ): Flow<StreamResource<String>> {
        val chat = generativeModel.startChat(
            history = history
        )
        var isStart = true
        val result = flow {

            val response = chat.sendMessageStream(prompt)
            response.collect {
                if (isStart) {
                    emit(StreamResource.Initiated(AiTools.GEMINI))
                    emit(StreamResource.StreamStarted(""))
                    isStart = false
                }
                SmartLog.d(TAG, "In-progress: ${it.text}")

                it.text?.let { text ->
                    val tokens = text.split(" ")
                    tokens.forEachIndexed { index, token ->
                        val modifiedToken = if (index != tokens.size - 1) "$token " else token
                        delay(10)
                        emit(StreamResource.StreamInProgress(modifiedToken))
                    }
                }
            }
            SmartLog.d(TAG, "Completed.")
            emit(StreamResource.StreamCompleted(true))

        }.catch { e ->
            SmartLog.e(TAG, e.stackTraceToString())
            emit(StreamResource.Error(RuntimeException(e.message)))
            Resource.Error(RuntimeException(e.message))
        }
        return result
    }

    companion object {
        private val TAG = Gemini::class.simpleName
    }
}