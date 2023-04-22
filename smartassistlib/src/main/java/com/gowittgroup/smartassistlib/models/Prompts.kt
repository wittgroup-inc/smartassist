package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class Prompts(@SerializedName("category") val category: PromptsCategory = PromptsCategory.EMPTY, @SerializedName("prompts") val prompts: List<String> = emptyList()) {
    companion object {
        val EMPTY = Prompts(PromptsCategory.EMPTY, emptyList())
        const val JOINING_DELIMITER = ':'
    }
}
