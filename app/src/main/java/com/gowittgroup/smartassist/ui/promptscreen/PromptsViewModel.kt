package com.gowittgroup.smartassist.ui.promptscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.models.Prompts
import com.gowittgroup.smartassistlib.models.successOr
import com.gowittgroup.smartassistlib.repositories.PromptsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PromptUiState(
    val prompts: List<Prompts> = emptyList(),
    val selectedPrompt: Prompts = Prompts.EMPTY,
    val loading: Boolean = false,
    val error: String = "",
)

@HiltViewModel
class PromptsViewModel @Inject constructor(private val repository: PromptsRepository, private val networkUtil: NetworkUtil, private val translations: PromptsScreenTranslations) :
    ViewModel() {
    private val _uiState = MutableStateFlow(PromptUiState(loading = true))
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    fun chooseChatModel(model: String) {
    }

    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            var error: String = ""
            var prompts : List<Prompts> = emptyList()
            if (networkUtil.isDeviceOnline()) {
                val promptsDeferred = async { repository.getAllPrompts() }
                 promptsDeferred.await().successOr(MutableSharedFlow(1)).collect{

                     _uiState.update { state->
                         state.copy(
                             loading = false,
                             prompts = it,
                             error = error
                         )
                     }
                }
            } else {
                prompts = emptyList()
                error = translations.noInternetConnectionMessage()
            }

            _uiState.update {
                it.copy(
                    loading = false,
                    prompts = prompts,
                    error = error
                )
            }
        }
    }

    fun resetErrorMessage() {
        _uiState.update { it.copy(error = "") }
    }

}

