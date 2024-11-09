package com.gowittgroup.smartassistlib.network

import com.gowittgroup.smartassistlib.Constants.CHAT_GPT_API_VERSION
import com.gowittgroup.smartassistlib.models.ChatCompletionRequest
import com.gowittgroup.smartassistlib.models.ChatCompletionResponse
import com.gowittgroup.smartassistlib.models.ModelResponse
import com.gowittgroup.smartassistlib.models.TextCompletionRequest
import com.gowittgroup.smartassistlib.models.TextCompletionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatGptService {
    @GET("$CHAT_GPT_API_VERSION/models")
    suspend fun getModels(): ModelResponse

    @POST("$CHAT_GPT_API_VERSION/completions")
    suspend fun sendTextMessage(@Body request: TextCompletionRequest): TextCompletionResponse

    @POST("$CHAT_GPT_API_VERSION/chat/completions")
    suspend fun sendChatMessage(@Body request: ChatCompletionRequest): ChatCompletionResponse
}
