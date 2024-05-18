package com.gowittgroup.smartassistlib.network

import com.gowittgroup.smartassistlib.Constants.API_VERSION
import com.gowittgroup.smartassistlib.models.ChatCompletionRequest
import com.gowittgroup.smartassistlib.models.ChatCompletionResponse
import com.gowittgroup.smartassistlib.models.ModelResponse
import com.gowittgroup.smartassistlib.models.TextCompletionRequest
import com.gowittgroup.smartassistlib.models.TextCompletionResponse
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
