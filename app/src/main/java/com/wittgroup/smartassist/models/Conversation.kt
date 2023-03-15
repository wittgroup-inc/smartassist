package com.wittgroup.smartassist.models

data class Conversation(val isQuestion: Boolean, val data: String, val isTyping: Boolean = true)
