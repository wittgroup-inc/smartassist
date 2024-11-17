package com.gowittgroup.smartassistlib.db.entities

import com.google.gson.annotations.SerializedName
import com.gowittgroup.smartassistlib.models.ai.AiTools

data class Conversation(
    @SerializedName("id") val id: String,
    @SerializedName("data") val data: String = "",
    @SerializedName("isQuestion") val isQuestion: Boolean = false,
    @SerializedName("replyFrom") val replyFrom: AiTools? = AiTools.NONE,
    @SerializedName("forSystem") val forSystem: Boolean = false,
    @SerializedName("referenceId") val referenceId: String = ""
)
