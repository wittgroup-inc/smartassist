package com.wittgroup.smartassist.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wittgroup.smartassistlib.db.entities.ConversationHistory
import com.wittgroup.smartassistlib.models.successOr
import com.wittgroup.smartassistlib.repositories.ConversationHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val conversationHistory: List<ConversationHistory> = emptyList(),
    val loading: Boolean = false
)

class HistoryViewModel(private val repository: ConversationHistoryRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState(loading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            // Trigger repository requests in parallel
            val historyDeferred = async { repository.getConversationHistory() }
            val history = historyDeferred.await().successOr(emptyList())

            _uiState.update {
                it.copy(
                    loading = false,
                    conversationHistory = history
                )
            }
        }
    }

    companion object {
        fun provideFactory(conversationHistoryRepository: ConversationHistoryRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HistoryViewModel(conversationHistoryRepository) as T
            }
        }
    }
}
