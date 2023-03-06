package com.wittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

/**
 * Sample
 *
 *
    {
    "model": "gpt-3.5-turbo",
    "messages": [{"role": "user", "content": "Hello!"}]
    }

 *
 */

data class ChatCompletionRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>
)
