package com.gowittgroup.smartassistlib.models.prompts

import com.google.gson.annotations.SerializedName

data class PromptsCategory(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("descriptions") val descriptions: String = ""
) {
    companion object {
        val EMPTY = PromptsCategory(0, "", "")
    }
}

