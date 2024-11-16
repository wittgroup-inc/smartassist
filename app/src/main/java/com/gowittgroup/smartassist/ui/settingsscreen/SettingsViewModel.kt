package com.gowittgroup.smartassist.ui.settingsscreen

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.successOr
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import com.gowittgroup.smartassistlib.repositories.authentication.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val networkUtil: NetworkUtil,
    private val translations: SettingScreenTranslations,
    private val authRepository: AuthenticationRepository,
) : BaseViewModelWithStateIntentAndSideEffect<SettingsUiState, SettingsIntent, SettingsSideEffects>() {

    init {
        refreshAll()
    }

    fun toggleReadAloud(isOn: Boolean) {
        viewModelScope.launch {
            repository.toggleReadAloud(isOn)
            uiState.value.copy(readAloud = isOn).applyStateUpdate()
        }
    }

    fun toggleHandsFreeMode(isOn: Boolean) {
        viewModelScope.launch {
            repository.toggleHandsFreeMode(isOn)
            uiState.value.copy(handsFreeMode = isOn).applyStateUpdate()
            refreshAll()
        }
    }

    fun chooseChatModel(model: String) {
        viewModelScope.launch {
            repository.chooseAiModel(model)
            uiState.value.copy(selectedAiModel = model).applyStateUpdate()
        }
    }

    fun chooseAiTool(tool: AiTools) {
        viewModelScope.launch {
            repository.chooseAiTool(tool)
            uiState.value.copy(selectedAiTool = tool).applyStateUpdate()
            refreshAll()
        }
    }

    private fun refreshAll() {

        uiState.value.copy(loading = true).applyStateUpdate()
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

            uiState.value.copy(
                loading = false,
                userId = userId,
                tools = tools,
                models = models,
                readAloud = readAloud,
                handsFreeMode = handsFreeMode,
                selectedAiModel = aiModel,
                selectedAiTool = aiTool,
                error = error
            ).applyStateUpdate()

        }
    }

    fun resetErrorMessage() {
        uiState.value.copy(error = "").applyStateUpdate()
    }

    fun logout() {
        viewModelScope.launch {
            val res = authRepository.signOut()
            when (res) {
                is Resource.Success -> sendSideEffect(SettingsSideEffects.SignOut)
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    override fun processIntent(intent: SettingsIntent) {
      // Need to implement
    }

    override fun getDefaultState(): SettingsUiState = SettingsUiState()

    fun onDeleteAccount() {
        viewModelScope.launch {
            val res = authRepository.deleteAccount()
            when (res) {
                is Resource.Success -> sendSideEffect(SettingsSideEffects.SignOut)
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }
}

