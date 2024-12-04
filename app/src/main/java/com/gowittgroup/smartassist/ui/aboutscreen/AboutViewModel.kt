package com.gowittgroup.smartassist.ui.aboutscreen

import com.gowittgroup.smartassist.core.BaseViewModelWithStateAndIntent
import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AboutUiState(
    val loading: Boolean = false,
    val notificationState: NotificationState? = null
): State

@HiltViewModel
class AboutViewModel @Inject constructor(
) : BaseViewModelWithStateAndIntent<AboutUiState, AboutIntent>() {

    override fun getDefaultState(): AboutUiState = AboutUiState()

    override fun processIntent(intent: AboutIntent) {

    }

    private fun publishErrorState(message: String) {
        uiState.value.copy(
            notificationState =
            NotificationState(
                message = message,
                type = NotificationType.ERROR,
                autoDismiss = true
            )

        ).applyStateUpdate()
    }

    fun clearNotification() {
        uiState.value.copy(
            notificationState = null
        ).applyStateUpdate()
    }
}

