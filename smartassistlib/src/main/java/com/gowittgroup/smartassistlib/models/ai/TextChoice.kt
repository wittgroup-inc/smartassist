package com.gowittgroup.smartassistlib.models.ai

import com.google.gson.annotations.SerializedName

data class TextChoice(
    @SerializedName("text") val text: String,
    @SerializedName("index") val index: Int,
    @SerializedName("logprobs") val logprobs: String?,
    @SerializedName("finish_reason") val finishReason: String
)
