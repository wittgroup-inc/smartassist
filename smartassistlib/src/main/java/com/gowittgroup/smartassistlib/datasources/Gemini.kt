package com.gowittgroup.smartassistlib.datasources


import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.gowittgroup.smartassistlib.Constants
import com.gowittgroup.smartassistlib.models.Message
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import com.gowittgroup.smartassistlib.models.successOr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class Gemini @Inject constructor(private val settingsDataSource: SettingsDataSource) : AiDataSource {


    override suspend fun getModels(): Resource<List<String>> {
        return Resource.Success(listOf(settingsDataSource.getDefaultChatModel()))
    }

    override suspend fun getReply(message: List<Message>): Resource<Flow<StreamResource<String>>> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "You will get reply from : Gemini")
            val model = settingsDataSource.getSelectedAiModel().successOr("")
            val generativeModel = GenerativeModel(
                modelName = model.ifEmpty { settingsDataSource.getDefaultChatModel() },
                apiKey = Constants.GEMINI_API_KEY
            )
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

            val chat = generativeModel.startChat(
                history = history
            )
            var isStart = true
            val result = flow {
                emit(StreamResource.StreamStarted(""))
                val response = chat.sendMessageStream(message.last().content!!)
                response.collect {
                    if(isStart){
                        emit(StreamResource.StreamStarted(""))
                        isStart = false
                    }
                    emit(StreamResource.StreamInProgress(it.text!!))
                }

                emit(StreamResource.StreamCompleted(true))
            }.catch { e ->
                Resource.Error(RuntimeException(e.message))
            }
            delay(500)
            Resource.Success(result)
        }

    companion object {
        private val TAG = Gemini::class.simpleName
    }
}