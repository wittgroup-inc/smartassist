package com.wittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class Choice(
    @SerializedName("text") val text: String,
    @SerializedName("index") val index: Int,
    @SerializedName("logprobs") val logprobs: String?,
    @SerializedName("finish_reason") val finishReason: String
)
