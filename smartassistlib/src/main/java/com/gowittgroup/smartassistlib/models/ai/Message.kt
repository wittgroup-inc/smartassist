package com.gowittgroup.smartassistlib.models.ai

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String? = ""
){
    companion object{
        const val ROLE_USER ="user"
        const val ROLE_ASSISTANT = "assistant"
        const val ROLE_SYSTEM = "system"
        const val ROLE_MODEL = "model"
    }

}
