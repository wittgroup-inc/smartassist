package com.gowittgroup.smartassistlib.models.ai


import com.google.gson.annotations.SerializedName

data class ModerationResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("results")
    val results: List<Result>
)