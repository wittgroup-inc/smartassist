package com.wittgroup.smartassistlib.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConversationHistory(
    @PrimaryKey val conversationId: Long,
    val conversations: List<Conversation>
)
