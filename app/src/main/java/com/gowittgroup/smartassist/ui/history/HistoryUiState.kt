package com.gowittgroup.smartassist.ui.history

import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory

data class HistoryUiState(
    val conversationHistory: List<ConversationHistory> = emptyList(),
    val query: String = "",
    val loading: Boolean = false
): State