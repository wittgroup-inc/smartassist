package com.wittgroup.smartassistlib.network

import com.wittgroup.smartassistlib.Constants.API_VERSION
import com.wittgroup.smartassistlib.models.ChatRequest
import com.wittgroup.smartassistlib.models.ChatResponse
import com.wittgroup.smartassistlib.models.ModelResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatGptService {
    @GET("$API_VERSION/models")
    suspend fun getModels(): ModelResponse

    @POST("$API_VERSION/completions")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}
