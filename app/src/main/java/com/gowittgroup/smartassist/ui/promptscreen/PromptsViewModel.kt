package com.gowittgroup.smartassist.ui.promptscreen

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateAndIntent
import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.domain.repositories.prompts.PromptsRepository
import com.gowittgroup.smartassistlib.models.prompts.Prompts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PromptUiState(
    val prompts: List<Prompts> = emptyList(),
    val selectedPrompt: Prompts = Prompts.EMPTY,
    val loading: Boolean = false,
    val notificationState: NotificationState? = null,
) : State

@HiltViewModel
class PromptsViewModel @Inject constructor(
    private val repository: PromptsRepository,
    private val networkUtil: NetworkUtil,
    private val translations: PromptsScreenTranslations
) : BaseViewModelWithStateAndIntent<PromptUiState, PromptIntent>() {

    override fun getDefaultState(): PromptUiState = PromptUiState()

    override fun processIntent(intent: PromptIntent) {
        TODO("Not yet implemented")
    }

    init {
        refreshAll()
    }

    private fun refreshAll() {
        uiState.value.copy(loading = true).applyStateUpdate()
        viewModelScope.launch {
            if (networkUtil.isDeviceOnline()) {
                val promptsDeferred = async { repository.getAllPrompts() }
                promptsDeferred.await()
                    .successOr(MutableSharedFlow(1)).collect {
                    uiState.value.copy(
                        loading = false,
                        prompts = it,
                    ).applyStateUpdate()
                }
            } else {
                uiState.value.copy(
                    loading = false,
                    prompts = emptyList(),
                    notificationState = getNotificationState(translations.noInternetConnectionMessage())
                ).applyStateUpdate()
            }
        }
    }

    fun clearNotification() {
        uiState.value.copy(notificationState = null).applyStateUpdate()
    }

    private fun publishErrorState(message: String) {
        uiState.value.copy(
            notificationState =
            getNotificationState(message)
        ).applyStateUpdate()
    }

    private fun getNotificationState(message: String) = NotificationState(
        message = message,
        type = NotificationType.ERROR
    )
}

