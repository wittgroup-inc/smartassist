package com.gowittgroup.smartassistlib.datasources

import android.util.Log
import com.google.gson.Gson
import com.gowittgroup.smartassistlib.Constants.API_VERSION
import com.gowittgroup.smartassistlib.Constants.BASE_URL
import com.gowittgroup.smartassistlib.KeyManager
import com.gowittgroup.smartassistlib.models.*
import com.gowittgroup.smartassistlib.network.ChatEventSourceListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val STREAM_COMPLETED_TOKEN = "[DONE]"

class ChatGpt @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val keyManager: KeyManager
) : AiDataSource {
    private val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()

    private val gson = Gson()

    override suspend fun getModels(): Resource<List<String>> {
        return Resource.Success(listOf(settingsDataSource.getDefaultChatModel()))
    }

    override suspend fun getReply(message: List<Message>): Resource<Flow<StreamResource<String>>> {
        Log.d(TAG, "You will get reply from : ChatGpt")
        var model = settingsDataSource.getSelectedAiModel().successOr("")
        if (model.isEmpty()) {
            model = settingsDataSource.getDefaultChatModel()
            settingsDataSource.chooseAiModel(model)
        }
        val userId = settingsDataSource.getUserId().successOr("")

        val result = MutableSharedFlow<StreamResource<String>>(1)
        return try {
            loadReply(
                message = message,
                model = model,
                userId = userId,
                listener = createChatEventSourceListener(result)
            )
            Resource.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
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
                Log.d(TAG, "Received Data: $data")
                runBlocking {
                    if (data != STREAM_COMPLETED_TOKEN) {
                        val response = gson.fromJson(data, ChatCompletionStreamResponse::class.java)
                        sendData(response)
                    } else {
                        Log.d(TAG, "Complete Response: $completeRes")
                        result.emit(StreamResource.StreamCompleted(true))
                    }
                }

            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                Log.e(TAG, t?.stackTraceToString() ?: "")
                coroutineScope.launch {
                    result.emit(StreamResource.Error(RuntimeException(response?.message)))
                }
            }

            private suspend fun sendData(response: ChatCompletionStreamResponse) {
                response.choices[0].delta.content?.let {
                    Log.d(TAG, "Parsed fine")
                    if (!started) {
                        result.emit(StreamResource.Initiated(AiTools.CHAT_GPT))
                        result.emit(StreamResource.StreamStarted(it.trimStart()))
                        Log.d(TAG, "OnStart: Sending to UI: ${it.trimStart()}")
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
        Log.d(TAG, "requestBody: $requestStr")
        val body = requestStr.toRequestBody(mediaType)
        makeRequest(body, listener)
    }

    private suspend fun makeRequest(body: RequestBody, listener: EventSourceListener) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$BASE_URL$API_VERSION/chat/completions")
                .header("Authorization", "Bearer ${keyManager.getOpenAiKey()}")
                .addHeader("Content-Type", "application/json")
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
