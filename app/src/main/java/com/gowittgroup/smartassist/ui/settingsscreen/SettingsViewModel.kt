package com.gowittgroup.smartassist.ui.settingsscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.ui.BaseViewModel
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassistlib.datasources.AuthenticationService
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.successOr
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val tools: List<AiTools> = emptyList(),
    val models: List<String> = emptyList(),
    val userId: String = "",
    val readAloud: Boolean = false,
    val handsFreeMode: Boolean = false,
    val selectedAiModel: String = "",
    val selectedAiTool: AiTools = AiTools.CHAT_GPT,
    val loading: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val networkUtil: NetworkUtil,
    private val translations: SettingScreenTranslations,
    private val authService: AuthenticationService,
) : BaseViewModel(authService) {
    private val _uiState = MutableStateFlow(SettingsUiState(loading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refreshAll()

        viewModelScope.launch {
            authService.currentUser.collect{
                Log.d("Pawan>>> Base", "user arrived......$it")
                if(it != null){
                    Session.updateCurrentUser(it)
                }
            }
        }
    }

    fun toggleReadAloud(isOn: Boolean) {
        viewModelScope.launch {
            repository.toggleReadAloud(isOn)
            _uiState.update { it.copy(readAloud = isOn) }
        }
    }

    fun toggleHandsFreeMode(isOn: Boolean) {
        viewModelScope.launch {
            repository.toggleHandsFreeMode(isOn)
            _uiState.update { it.copy(handsFreeMode = isOn) }
            refreshAll()
        }
    }

    fun chooseChatModel(model: String) {
        viewModelScope.launch {
            repository.chooseAiModel(model)
            _uiState.update { it.copy(selectedAiModel = model) }
        }
    }

    fun chooseAiTool(tool: AiTools) {
        viewModelScope.launch {
            repository.chooseAiTool(tool)
            _uiState.update { it.copy(selectedAiTool = tool) }
            refreshAll()
        }
    }

    private fun refreshAll() {

        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            var error: String = ""
            // Trigger repository requests in parallel
            val userIdDeferred = async { repository.getUserId() }
            val userId = userIdDeferred.await().successOr("")
            val models: List<String>
            val tools: List<AiTools>
            if (networkUtil.isDeviceOnline()) {
                val modelsDeferred = async { repository.getModels() }
                models = modelsDeferred.await().successOr(emptyList())
            } else {
                models = emptyList()
                error = translations.noInternetConnectionMessage()
            }

            val toolsDeferred = async { repository.getAiTools() }
            tools = toolsDeferred.await().successOr(emptyList())

            val readAloudDeferred = async { repository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)

            val handsFreeModeDeferred = async { repository.getHandsFreeMode() }
            val handsFreeMode = handsFreeModeDeferred.await().successOr(false)

            val aiModelDeferred = async { repository.getSelectedAiModel() }
            val aiModel = aiModelDeferred.await().successOr("")

            val aiToolDeferred = async { repository.getSelectedAiTool() }
            val aiTool = aiToolDeferred.await().successOr(AiTools.CHAT_GPT)

            _uiState.update {
                it.copy(
                    loading = false,
                    userId = userId,
                    tools = tools,
                    models = models,
                    readAloud = readAloud,
                    handsFreeMode = handsFreeMode,
                    selectedAiModel = aiModel,
                    selectedAiTool = aiTool,
                    error = error
                )
            }
        }
    }

    fun resetErrorMessage() {
        _uiState.update { it.copy(error = "") }
    }

    fun logout() {
        viewModelScope.launch {
            authService.signOut()
        }
    }

}

