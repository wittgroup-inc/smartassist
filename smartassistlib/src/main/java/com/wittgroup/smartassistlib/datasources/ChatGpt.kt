package com.wittgroup.smartassistlib.datasources

import android.util.Log
import com.wittgroup.smartassistlib.models.ChatRequest
import com.wittgroup.smartassistlib.models.ChatResponse
import com.wittgroup.smartassistlib.models.ModelResponse
import com.wittgroup.smartassistlib.models.Resource
import com.wittgroup.smartassistlib.network.ChatGptService
import com.wittgroup.smartassistlib.network.NetworkHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatGpt : AI {

    private val service: ChatGptService by lazy { NetworkHelper.getRetrofit().create(ChatGptService::class.java) }

    override suspend fun getModels(): Resource<List<String>> {
        return try {
            val response = service.getModels().data.map { it.id }
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e)
        }


//
//        service.getModels().enqueue(object : Callback<ModelResponse> {
//            override fun onResponse(call: Call<ModelResponse>, response: Response<ModelResponse>) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, response.body()?.data?.map { it.id }?.toTypedArray().contentToString())
//                }
//            }
//
//            override fun onFailure(call: Call<ModelResponse>, t: Throwable) {
//                Log.e(TAG, "Failed to load models, error: ${t.message}")
//            }
//
//        })
//        return listOf()
    }

    override suspend fun getAnswer(query: String): Resource<String> {

        return try {
            val response =
                service.sendMessage(ChatRequest(model = "text-davinci-003", prompt = query, temperature = 0, maxTokens = 1000)).choices?.get(0)?.text
            Log.d(TAG, "Answer: $response")
            Resource.Success(response!!)
        } catch (e: Exception) {
            Resource.Error(e)
        }
//        service.sendMessage()
//            .enqueue(object : Callback<ChatResponse> {
//                override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
//                    if (response.isSuccessful) {
//                        Log.d(TAG, "Answer: ${response.body()?.choices?.get(0)?.text}")
//                    }
//                }
//
//                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
//                    Log.e(TAG, "Failed to load answer, error: ${t.message}")
//                }
//
//            })
//        return ""
    }

    companion object {
        private val TAG = ChatGpt::class.simpleName
    }
}
