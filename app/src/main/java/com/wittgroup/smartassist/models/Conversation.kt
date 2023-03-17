package com.wittgroup.smartassist.models

import kotlinx.coroutines.flow.MutableStateFlow

data class Conversation(val isQuestion: Boolean, val data: MutableStateFlow<String>, val isTyping: Boolean = true)
