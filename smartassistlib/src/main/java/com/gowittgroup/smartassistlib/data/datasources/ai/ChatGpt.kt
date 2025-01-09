package com.gowittgroup.smartassistlib.data.datasources.ai

import com.google.gson.Gson
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.mappers.toMessages
import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.models.ai.ChatCompletionRequest
import com.gowittgroup.smartassistlib.models.ai.ChatCompletionStreamResponse
import com.gowittgroup.smartassistlib.models.ai.Message
import com.gowittgroup.smartassistlib.network.ChatEventSourceListener
import com.gowittgroup.smartassistlib.util.Constants
import com.gowittgroup.smartassistlib.util.KeyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject

private const val STREAM_COMPLETED_TOKEN = "[DONE]"

class ChatGpt @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val keyManager: KeyManager,
    private val client: OkHttpClient,
    private val gson: Gson
) : AiDataSource {

    override suspend fun getModels(): Resource<List<String>> {
        return Resource.Success(listOf(settingsDataSource.getDefaultChatModel()))
    }

    override suspend fun getReply(conversations: List<Conversation>): Resource<Flow<StreamResource<String>>> {
        SmartLog.d(TAG, "You will get reply from : ChatGpt")
        var model = settingsDataSource.getSelectedAiModel().successOr("")
        if (model.isEmpty()) {
            model = settingsDataSource.getDefaultChatModel()
            settingsDataSource.chooseAiModel(model)
        }
        val userId = settingsDataSource.getUserId().successOr("")

        val result = MutableSharedFlow<StreamResource<String>>(1)
        return try {
            loadReply(
                message = conversations.toMessages(),
                model = model,
                userId = userId,
                listener = createChatEventSourceListener(result)
            )
            Resource.Success(result)
        } catch (e: Exception) {
            SmartLog.e(TAG, e.stackTraceToString())
            Resource.Error(e)
        }

    }

    private fun createChatEventSourceListener(result: MutableSharedFlow<StreamResource<String>>) =
        object : ChatEventSourceListener() {
            var completeRes = ""
            var started = false
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                super.onEvent(eventSource, id, type, data)
                SmartLog.d(TAG, "Received Data: $data")
                runBlocking {
                    if (data != STREAM_COMPLETED_TOKEN) {
                        val response = gson.fromJson(data, ChatCompletionStreamResponse::class.java)
                        sendData(response)
                    } else {
                        SmartLog.d(TAG, "Complete Response: $completeRes")
                        result.emit(StreamResource.StreamCompleted(true))
                    }
                }

            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                SmartLog.e(TAG, t?.stackTraceToString() ?: "")
                coroutineScope.launch {
                    result.emit(StreamResource.Error(RuntimeException(response?.message)))
                }
            }

            private suspend fun sendData(response: ChatCompletionStreamResponse) {
                response.choices[0].delta.content?.let {
                    SmartLog.d(TAG, "Parsed fine")
                    if (!started) {
                        result.emit(StreamResource.Initiated(AiTools.CHAT_GPT))
                        result.emit(StreamResource.StreamStarted(it.trimStart()))
                        SmartLog.d(TAG, "OnStart: Sending to UI: ${it.trimStart()}")
                        started = true
                    } else {
                        completeRes += it
                        result.emit(StreamResource.StreamInProgress(it))
                    }
                }
            }
        }

    private suspend fun loadReply(
        message: List<Message>,
        model: String,
        userId: String,
        listener: EventSourceListener
    ) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestStr =
            gson.toJson(ChatCompletionRequest(model = model, messages = message, user = userId))
        SmartLog.d(TAG, "requestBody: $requestStr")
        val body = requestStr.toRequestBody(mediaType)
        makeRequest(body, listener)
    }

    private suspend fun makeRequest(body: RequestBody, listener: EventSourceListener) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("${Constants.CHAT_GPT_BASE_URL}${Constants.CHAT_GPT_API_VERSION}/chat/completions")
                .addHeader("Accept", "text/event-stream")
                .post(body)
                .build()

            EventSources.createFactory(client)
                .newEventSource(request = request, listener = listener)
        }
    }

    companion object {
        private val TAG = ChatGpt::class.simpleName
    }
}