package com.wittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class ChatCompletionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("created") val created: Int,
    @SerializedName("model") var model: String,
    @SerializedName("choices") var choices: List<MessageChoice>,
    @SerializedName("usage") var usage: Usage
)
