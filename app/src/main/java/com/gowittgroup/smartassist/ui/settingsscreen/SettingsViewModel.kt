package com.gowittgroup.smartassist.ui.settingsscreen

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassist.ui.settingsscreen.translations.SettingScreenTranslations
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.domain.repositories.settings.SettingsRepository
import com.gowittgroup.smartassistlib.models.ai.AiTools
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
                tools = tools,
                models = models,
                readAloud = readAloud,
                handsFreeMode = handsFreeMode,
                selectedAiModel = aiModel,
                selectedAiTool = aiTool,
                notificationState = if (error.isNotBlank()) getErrorState(error) else null
            ).applyStateUpdate()

        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            GoogleSignIn.getClient(
                context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut()
            when (val res = authRepository.signOut()) {
                is Resource.Success -> sendSideEffect(SettingsSideEffects.SignOut)
                is Resource.Error -> publishErrorState(
                    res.exception.message ?: "Something went wrong."
                )
            }
        }
    }

    override fun processIntent(intent: SettingsIntent) {
        // Need to implement
    }

    override fun getDefaultState(): SettingsUiState = SettingsUiState()

    fun onDeleteAccount() {
        viewModelScope.launch {
            when (val res = authRepository.deleteAccount()) {
                is Resource.Success -> sendSideEffect(SettingsSideEffects.SignOut)
                is Resource.Error -> publishErrorState(
                    res.exception.message ?: "Something went wrong."
                )
            }
        }
    }

    private fun publishErrorState(message: String) {
        uiState.value.copy(
            notificationState = getErrorState(message)
        ).applyStateUpdate()
    }

    private fun getErrorState(message: String) =

        NotificationState(
            message = message,
            type = NotificationType.ERROR
        )


    fun onNotificationClose() {
        uiState.value.copy(
            notificationState = null
        ).applyStateUpdate()
    }
}

