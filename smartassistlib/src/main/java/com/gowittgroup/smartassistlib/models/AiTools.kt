package com.gowittgroup.smartassistlib.models

import com.gowittgroup.smartassistlib.Constants

enum class AiTools(val defaultModel: String, val displayName: String) {
    NONE("", ""), CHAT_GPT(defaultModel = Constants.CHAT_GPT_DEFAULT_CHAT_AI_MODEL, displayName = "ChatGPT"), GEMINI(defaultModel = Constants.GEMINI_DEFAULT_CHAT_AI_MODEL,  displayName = "Gemini")
}