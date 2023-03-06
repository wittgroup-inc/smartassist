package com.wittgroup.smartassistlib.network

import com.wittgroup.smartassistlib.Constants.API_VERSION
import com.wittgroup.smartassistlib.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatGptService {
    @GET("$API_VERSION/models")
    suspend fun getModels(): ModelResponse

    @POST("$API_VERSION/completions")
    suspend fun sendTextMessage(@Body request: TextCompletionRequest): TextCompletionResponse

    @POST("$API_VERSION/chat/completions")
    suspend fun sendChatMessage(@Body request: ChatCompletionRequest): ChatCompletionResponse
}
