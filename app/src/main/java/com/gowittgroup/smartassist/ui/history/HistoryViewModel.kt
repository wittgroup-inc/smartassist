package com.gowittgroup.smartassist.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.models.successOr
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val conversationHistory: List<ConversationHistory> = emptyList(),
    val loading: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: ConversationHistoryRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState(loading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            // Trigger repository requests in parallel
                repository.getConversationHistory().successOr(flow { }).collect { history ->
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

}
