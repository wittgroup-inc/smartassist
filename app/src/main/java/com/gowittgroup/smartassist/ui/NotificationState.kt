package com.gowittgroup.smartassist.ui

import com.gowittgroup.smartassist.ui.components.NotificationType

data class NotificationState(
    val message: String,
    val type: NotificationType,
    val autoDismiss: Boolean = true
)