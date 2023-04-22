package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class Prompts(@SerializedName("category") val category: PromptsCategory, @SerializedName("prompts") val prompts: List<String>) {
    companion object {
        val EMPTY = Prompts(PromptsCategory.EMPTY, emptyList())
    }
}
