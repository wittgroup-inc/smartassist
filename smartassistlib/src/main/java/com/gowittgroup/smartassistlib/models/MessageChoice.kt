package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class MessageChoice(
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: Message,
    @SerializedName("finish_reason") val finishReason: String
)
