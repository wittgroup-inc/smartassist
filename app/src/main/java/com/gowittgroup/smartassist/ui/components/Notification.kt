package com.gowittgroup.smartassist.ui.components

import androidx.compose.runtime.Composable
import com.gowittgroup.smartassist.ui.NotificationState

@Composable
internal fun Notification(
    notificationState: NotificationState,
    onNotificationClose: () -> Unit
) {
    if (notificationState.autoDismiss) {
        AutoDismissibleInAppNotification(
            message = notificationState.message,
            type = notificationState.type,
            onClose = onNotificationClose
        )
    } else {
        InAppNotification(
            message = notificationState.message,
            type = notificationState.type,
            onClose = onNotificationClose
        )
    }
}