package com.wittgroup.smartassistlib.datasources

import android.util.Log
import com.wittgroup.smartassistlib.models.*
import com.wittgroup.smartassistlib.network.ChatGptService
import com.wittgroup.smartassistlib.network.NetworkHelper

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
            model = "text-davinci-003"
            settingsDataSource.chooseAiModel(model)
        }
        return try {
            val response =
                service.sendMessage(ChatRequest(model = model, prompt = query, temperature = 0, maxTokens = 1000)).choices?.get(0)?.text
            Log.d(TAG, "Answer: $response")
            Resource.Success(response!!)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    companion object {
        private val TAG = ChatGpt::class.simpleName
    }
}
