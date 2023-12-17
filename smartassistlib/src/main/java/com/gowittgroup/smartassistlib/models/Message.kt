package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String? = ""
){
    companion object{
        val ROLE_USER ="user"
        val ROLE_ASSISTANT = "assistant"
        val ROLE_SYSTEM = "system"

        val ROLE_MODEL = "model"
    }

}
