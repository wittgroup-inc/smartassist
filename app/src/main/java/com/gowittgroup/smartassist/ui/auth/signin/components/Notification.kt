package com.gowittgroup.smartassist.ui.auth.signin.components

import androidx.compose.runtime.Composable
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.AutoDismissibleInAppNotification
import com.gowittgroup.smartassist.ui.components.InAppNotification

@Composable
internal fun Notification(
    notificationState: NotificationState?,
    onNotificationClose: () -> Unit,
    switchToSignIn: () -> Unit
) {
    notificationState?.let {
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
                onClose = {
                    onNotificationClose()
                    switchToSignIn()
                }
            )
        }
    }
}