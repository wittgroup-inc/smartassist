package com.gowittgroup.smartassist.ui.history

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateAndIntent
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.domain.repositories.converstationhistory.ConversationHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ConversationHistoryRepository
) : BaseViewModelWithStateAndIntent<HistoryUiState, HistoryIntent>() {

    private var historyCache = listOf<ConversationHistory>()

    init {
        refreshAll()
    }

    private fun refreshAll() {
        uiState.value.copy(loading = true).applyStateUpdate()
        viewModelScope.launch(Dispatchers.IO) {

            repository.getConversationHistory().successOr(flow { }).collect { history ->
                historyCache = history
                uiState.value.copy(
                    loading = false,
                    conversationHistory = history
                ).applyStateUpdate()
            }
        }
    }

    fun deleteHistory(conversationHistory: ConversationHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearConversationHistory(conversationHistory)
        }
    }

    fun search(q: String) {
        if (q.isEmpty()) uiState.value.copy(
            query = q,
            conversationHistory = historyCache
        ).applyStateUpdate()
        else
            uiState.value.copy(
                query = q,
                conversationHistory = historyCache.filter { history ->
                    containsResultForQuery(
                        q,
                        history
                    )
                }).applyStateUpdate()

    }

    private fun containsResultForQuery(q: String, history: ConversationHistory): Boolean =
        history.conversations.any { it.data.contains(q, true) }

    override fun getDefaultState(): HistoryUiState = HistoryUiState()

    override fun processIntent(intent: HistoryIntent) {

    }
}
