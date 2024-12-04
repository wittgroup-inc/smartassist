package com.gowittgroup.smartassist.ui.auth.signup.components

import androidx.compose.runtime.Composable
import com.gowittgroup.smartassist.ui.auth.signup.SignUpUiState
import com.gowittgroup.smartassist.ui.components.AutoDismissibleInAppNotification
import com.gowittgroup.smartassist.ui.components.InAppNotification

@Composable
internal fun Notification(
    uiState: SignUpUiState,
    onNotificationClose: () -> Unit,
    closeNotificationAndNavigateToLogin: () -> Unit
) {
    uiState.notificationState?.let {
        if (it.autoDismiss) {
            AutoDismissibleInAppNotification(
                message = it.message,
                type = it.type,
                onClose = onNotificationClose
            )
        } else {
            InAppNotification(
                message = it.message,
                type = it.type,
                onClose = closeNotificationAndNavigateToLogin
            )
        }
    }
}