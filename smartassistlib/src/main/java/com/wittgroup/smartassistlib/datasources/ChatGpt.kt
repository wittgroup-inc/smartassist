package com.wittgroup.smartassistlib.datasources

import com.google.gson.Gson
import com.wittgroup.smartassistlib.Constants
import com.wittgroup.smartassistlib.Constants.API_VERSION
import com.wittgroup.smartassistlib.Constants.BASE_URL
import com.wittgroup.smartassistlib.models.*
import com.wittgroup.smartassistlib.network.ChatEventSourceListener
import com.wittgroup.smartassistlib.network.ChatGptService
import com.wittgroup.smartassistlib.network.NetworkHelper
import kotlinx.coroutines.delay
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
import kotlin.time.milliseconds

private const val DEFAULT_AI_MODEL = "text-davinci-003"
private const val CHAT_DEFAULT_AI_MODEL = "gpt-3.5-turbo"
private const val MAX_TOKEN = 2048

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

    override suspend fun getAnswer(query: String): Resource<String> {
        var model = settingsDataSource.getSelectedAiModel().successOr("")
        if (model.isEmpty()) {
            model = "text-davinci-003"
            settingsDataSource.chooseAiModel(model)
        }
        val result = MutableSharedFlow<String>(1)
        return try {
            load(query, object : ChatEventSourceListener() {
                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    super.onEvent(eventSource, id, type, data)
                    if (data != "[DONE]") {
                        val response = gson.fromJson(data, ChatResponse::class.java)
                        result.tryEmit(response.choices[0].text)

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

    override suspend fun getReply(message: String): Resource<Flow<String>> {
        TODO("Not yet implemented")
    }

    companion object {
        private val TAG = ChatGpt::class.simpleName
    }


    private fun load(query: String, listener: EventSourceListener) {


        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestStr = gson.toJson(ChatRequest(model = "text-davinci-003", prompt = query, temperature = 0, maxTokens = 1000))
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
}
