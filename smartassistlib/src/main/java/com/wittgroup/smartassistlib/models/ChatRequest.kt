package com.wittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

/**
 * Sample
 *
 *
        {
        "model": "text-davinci-003",
        "prompt": "Android archtecture",
        "temperature": 0,
        "max_tokens": 1000
        }

 *
 */

data class ChatRequest(
    @SerializedName("model") val model: String,
    @SerializedName("prompt") val prompt: String,
    @SerializedName("temperature") val temperature: Int,
    @SerializedName("max_tokens") val maxTokens: Int
)
