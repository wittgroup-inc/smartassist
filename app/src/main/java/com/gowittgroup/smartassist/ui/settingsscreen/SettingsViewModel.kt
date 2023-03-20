package com.gowittgroup.smartassist.ui.settingsscreen

import androidx.lifecycle.*
import com.gowittgroup.smartassistlib.models.successOr
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val models: List<String> = emptyList(),
    val userId: String = "",
    val readAloud: Boolean = false,
    val selectedAiModel: String = "",
    val loading: Boolean = false
)

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState(loading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    fun toggleReadAloud(isOn: Boolean) {
        viewModelScope.launch {
            repository.toggleReadAloud(isOn)
            _uiState.update { it.copy(readAloud = isOn) }
        }
    }

    fun chooseChatModel(model: String) {
        viewModelScope.launch {
            repository.chooseAiModel(model)
            _uiState.update { it.copy(selectedAiModel = model) }
        }
    }

    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            // Trigger repository requests in parallel
            val userIdDeferred = async { repository.getUserId() }
            val userId = userIdDeferred.await().successOr("")

            val modelsDeferred = async { repository.getModels() }
            val models = modelsDeferred.await().successOr(emptyList())

            val readAloudDeferred = async { repository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)

            val aiModelDeferred = async { repository.getSelectedAiModel() }
            val aiModel = aiModelDeferred.await().successOr("")

            _uiState.update {
                it.copy(
                    loading = false,
                    userId = userId,
                    models = models,
                    readAloud = readAloud,
                    selectedAiModel = aiModel
                )
            }
        }
    }

    companion object {
        fun provideFactory(settingsRepository: SettingsRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(settingsRepository) as T
            }
        }
    }
}

