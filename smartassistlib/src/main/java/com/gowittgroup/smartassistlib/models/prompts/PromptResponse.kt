package com.gowittgroup.smartassistlib.models.prompts

import com.google.gson.annotations.SerializedName

data class PromptResponse(@SerializedName("data") val data: List<Prompts> = emptyList())

