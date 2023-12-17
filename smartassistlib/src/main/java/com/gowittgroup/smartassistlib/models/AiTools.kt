package com.gowittgroup.smartassistlib.models

enum class AiTools(val defaultModel: String, val displayName: String) {

    NONE("", ""), CHAT_GPT(defaultModel = "gpt-3.5-turbo", displayName = "ChatGPT"), GEMINI(defaultModel = "gemini-pro",  displayName = "Gemini")


}