package com.gowittgroup.smartassist.models

import kotlinx.coroutines.flow.MutableStateFlow

data class Conversation(val id: String, val isQuestion: Boolean = false, val data: String= "", val stream: MutableStateFlow<String>, val isTyping: Boolean = true, val isLoading:Boolean = false, val forSystem: Boolean = false, val referenceId: String = "")
