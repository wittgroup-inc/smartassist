package com.gowittgroup.smartassist.ui.history

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModel
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.models.successOr
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.repositories.authentication.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val conversationHistory: List<ConversationHistory> = emptyList(),
    val query: String = "",
    val loading: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ConversationHistoryRepository,
    private val authRepository: AuthenticationRepository
) : BaseViewModel(authRepository) {

    private val _uiState = MutableStateFlow(HistoryUiState(loading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    private var historyCache = listOf<ConversationHistory>()

    init {
        refreshAll()
    }

    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            // Trigger repository requests in parallel
            repository.getConversationHistory().successOr(flow { }).collect { history ->
                historyCache = history
                _uiState.update {
                    it.copy(
                        loading = false,
                        conversationHistory = history
                    )
                }
            }
        }
    }

    fun deleteHistory(conversationHistory: ConversationHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearConversationHistory(conversationHistory)
        }
    }

    fun search(q: String) {
        if (q.isEmpty()) _uiState.update {
            it.copy(
                query = q,
                conversationHistory = historyCache
            )
        }
        else
            _uiState.update {
                it.copy(
                    query = q,
                    conversationHistory = historyCache.filter { history ->
                        containsResultForQuery(
                            q,
                            history
                        )
                    })
            }
    }

    private fun containsResultForQuery(q: String, history: ConversationHistory): Boolean =
        history.conversations.any { it.data.contains(q, true) }


}
