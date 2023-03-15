package com.wittgroup.smartassistlib.datasources

import android.util.Log
import com.wittgroup.smartassistlib.models.*
import com.wittgroup.smartassistlib.network.ChatGptService
import com.wittgroup.smartassistlib.network.NetworkHelper

private const val DEFAULT_AI_MODEL = "text-davinci-003"
private const val CHAT_DEFAULT_AI_MODEL = "gpt-3.5-turbo"
private const val MAX_TOKEN = 2048

class ChatGpt(private val settingsDataSource: SettingsDataSource) : AiDataSource {

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
            model = DEFAULT_AI_MODEL
            settingsDataSource.chooseAiModel(model)
        }
        return try {
            val response =
                service.sendTextMessage(
                    TextCompletionRequest(
                        model = model,
                        prompt = query,
                        temperature = 0,
                        maxTokens = MAX_TOKEN
                    )
                ).choices?.get(0)?.text
            Log.d(TAG, "Q: $query, A: $response")
            Resource.Success(response!!)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            Resource.Error(e)
        }
    }

    override suspend fun getReply(message: String): Resource<String> {
        var model = settingsDataSource.getSelectedAiModel().successOr("")
        if (model.isEmpty()) {
            model = CHAT_DEFAULT_AI_MODEL
            settingsDataSource.chooseAiModel(model)
        }
        return try {
            val response =
                service.sendChatMessage(
                    ChatCompletionRequest(
                        model = model,
                        messages = listOf(Message(role = "user", content = message))
                    )
                ).choices[0].message.content
            Log.d(TAG, "Q: $message, A: $response")
            Resource.Success(response!!)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
            Resource.Error(e)
        }
    }

    companion object {
        private val TAG = ChatGpt::class.simpleName
    }
}
