package com.gowittgroup.smartassistlib.db.entities

import com.google.gson.annotations.SerializedName

data class Conversation(
    @SerializedName("data") val data: String = "",
    @SerializedName("isQuestion") val isQuestion: Boolean = false,
    @SerializedName("forSystem") val forSystem: Boolean = false
)
