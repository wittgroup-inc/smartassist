package com.gowittgroup.smartassistlib.models.ai


import com.google.gson.annotations.SerializedName

data class ModerationRequest(
    @SerializedName("input")
    val input: String,
    @SerializedName("model")
    val model: String = "omni-moderation-latest"
)