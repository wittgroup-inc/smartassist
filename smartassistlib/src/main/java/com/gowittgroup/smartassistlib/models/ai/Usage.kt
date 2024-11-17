package com.gowittgroup.smartassistlib.models.ai

import com.google.gson.annotations.SerializedName

class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)
