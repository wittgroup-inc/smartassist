package com.gowittgroup.smartassistlib.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ConversationHistory(
    @PrimaryKey val conversationId: Long,
    val conversations: List<Conversation>,
    val timestamp: Date = Date()
) {
    companion object {
        val DEFAULT = ConversationHistory(conversationId = getId(), conversations = mutableListOf())
        fun getId() = run { UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE }
    }
}
