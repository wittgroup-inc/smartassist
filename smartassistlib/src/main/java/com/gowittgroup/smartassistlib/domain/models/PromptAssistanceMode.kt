package com.gowittgroup.smartassistlib.domain.models

sealed class PromptAssistanceMode { object NORMAL; object ASSIST; object TEMPLATE }

data class ClarifyingQuestion(val id: String, val question: String)
data class Template(val id: String, val title: String, val description: String, val placeholders: List<String>)
data class PromptAssembly(val assembledPrompt: String)