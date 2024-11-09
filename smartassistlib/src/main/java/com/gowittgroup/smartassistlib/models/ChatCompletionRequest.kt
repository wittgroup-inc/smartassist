package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName


/**
 *  SAMPLE REQUEST
 *
 *   {
 *     "model": "gpt-3.5-turbo",
 *     "messages": [{"role": "user", "content": "Hello!"}]
 *   }
 */


data class ChatCompletionRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("stream") val stream: Boolean = true,
    @SerializedName("user") val user: String,
)
