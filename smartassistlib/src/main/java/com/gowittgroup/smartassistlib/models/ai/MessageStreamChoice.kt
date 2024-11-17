package com.gowittgroup.smartassistlib.models.ai

import com.google.gson.annotations.SerializedName

data class MessageStreamChoice(
    @SerializedName("index") val index: Int,
    @SerializedName("delta") val delta: Message,
    @SerializedName("finish_reason") val finishReason: String
)
