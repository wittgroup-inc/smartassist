package com.gowittgroup.smartassist.models

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

typealias ConversationEntity = com.gowittgroup.smartassistlib.db.entities.Conversation

data class Conversation(val id: String, val isQuestion: Boolean = false, val data: String= "", val stream: MutableStateFlow<String>, val isTyping: Boolean = false, val isLoading:Boolean = false, val forSystem: Boolean = false, val referenceId: String = "")
fun Conversation.toConversationEntity(): ConversationEntity = with(this) {
    ConversationEntity(
        id = id,
        isQuestion = isQuestion,
        data = data,
        forSystem = forSystem,
        referenceId = referenceId
    )}

fun ConversationEntity.toConversation(): Conversation = with(this){
    Conversation(
        id = id ?: UUID.randomUUID().toString(),
        isQuestion = isQuestion,
        data = data,
        stream = MutableStateFlow(data),
        isTyping = false,
        forSystem = forSystem,
        referenceId = referenceId ?: ""
    )
}