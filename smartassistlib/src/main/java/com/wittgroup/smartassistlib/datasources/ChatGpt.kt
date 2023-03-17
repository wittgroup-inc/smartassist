package com.wittgroup.smartassistlib.datasources

import android.util.Log
import com.google.gson.Gson
import com.wittgroup.smartassistlib.Constants
import com.wittgroup.smartassistlib.Constants.API_VERSION
import com.wittgroup.smartassistlib.Constants.BASE_URL
import com.wittgroup.smartassistlib.models.*
import com.wittgroup.smartassistlib.network.ChatEventSourceListener
import com.wittgroup.smartassistlib.network.ChatGptService
import com.wittgroup.smartassistlib.network.NetworkHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

private const val DEFAULT_AI_MODEL = "text-davinci-003"
private const val CHAT_DEFAULT_AI_MODEL = "gpt-3.5-turbo"
private const val MAX_TOKEN = 2048
private const val STREAM_COMPLETED_TOKEN = "[DONE]"

class ChatGpt(private val settingsDataSource: SettingsDataSource) : AiDataSource {
    private val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()

    private val gson = Gson()

    private val service: ChatGptService by lazy { NetworkHelper.getRetrofit().create(ChatGptService::class.java) }

    override suspend fun getModels(): Resource<List<String>> {
        return try {
            val response = service.getModels().data.map { it.id }
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e)
        }

    }

    override suspend fun getAnswer(query: String): Resource<Flow<StreamResource<String>>> {
        var model = settingsDataSource.getSelectedAiModel().successOr("")
        if (model.isEmpty()) {
            model = DEFAULT_AI_MODEL
            settingsDataSource.chooseAiModel(model)
        }
        val result = MutableSharedFlow<StreamResource<String>>(1)
        return try {
            var started = false
            load(query, object : ChatEventSourceListener() {
                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    super.onEvent(eventSource, id, type, data)
                    if (data != STREAM_COMPLETED_TOKEN) {
                        val response = gson.fromJson(data, ChatCompletionStreamResponse::class.java)
                        response.choices[0].delta.content?.let {
                            if (!started) {
                                StreamResource.StreamStarted(result)
                                result.tryEmit(StreamResource.StreamStarted(it.trimStart()))
                                started = true
                            } else {
                                result.tryEmit(StreamResource.StreamInProgress(it))
                            }
                        }
                    } else {
                        result.tryEmit(StreamResource.StreamCompleted(true))
                    }
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                    super.onFailure(eventSource, t, response)
                    StreamResource.Error(RuntimeException(response?.message))
                }
            })
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e)
        }

    }

    override suspend fun getReply(message: String): Resource<Flow<StreamResource<String>>> {
        var model = settingsDataSource.getSelectedAiModel().successOr("")
        if (model.isEmpty()) {
            model = CHAT_DEFAULT_AI_MODEL
            settingsDataSource.chooseAiModel(model)
        }
        val result = MutableSharedFlow<StreamResource<String>>(1)
        return try {
            var started = false
            loadReply(message, object : ChatEventSourceListener() {
                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    Log.d(TAG, "Received...")
                    super.onEvent(eventSource, id, type, data)
                    if (data != STREAM_COMPLETED_TOKEN) {
                        Log.d(TAG, "Not done")
                        val response = gson.fromJson(data, ChatCompletionStreamResponse::class.java)
                        response.choices[0].delta.content?.let {
                            Log.d(TAG, "Parsed fine")
                            if (!started) {
                                Log.d(TAG, "Not yet started")
                                result.tryEmit(StreamResource.StreamStarted(it.trimStart()))
                                started = true
                                Log.d(TAG, "started")
                            } else {
                                result.tryEmit(StreamResource.StreamInProgress(it))
                            }
                        }
                    } else {
                        Log.d(TAG, "Stream Completed")
                        result.tryEmit(StreamResource.StreamCompleted(true))
                    }
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                    super.onFailure(eventSource, t, response)
                    Resource.Error(RuntimeException(response?.message))
                }
            })
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    private fun load(query: String, listener: EventSourceListener) {

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestStr = gson.toJson(TextCompletionRequest(model = DEFAULT_AI_MODEL, prompt = query, temperature = 0, maxTokens = MAX_TOKEN))
        val body = requestStr.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL$API_VERSION/completions")
            .header("Authorization", "Bearer ${Constants.API_KEY}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "text/event-stream")
            .post(body)
            .build()

        EventSources.createFactory(client).newEventSource(request = request, listener = listener)
    }

    private fun loadReply(message: String, listener: EventSourceListener) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestStr =
            gson.toJson(ChatCompletionRequest(model = CHAT_DEFAULT_AI_MODEL, messages = listOf(Message(role = "user", content = message))))
        val body = requestStr.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL$API_VERSION/chat/completions")
            .header("Authorization", "Bearer ${Constants.API_KEY}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "text/event-stream")
            .post(body)
            .build()

        EventSources.createFactory(client).newEventSource(request = request, listener = listener)
    }

    companion object {
        private val TAG = ChatGpt::class.simpleName
    }
}
