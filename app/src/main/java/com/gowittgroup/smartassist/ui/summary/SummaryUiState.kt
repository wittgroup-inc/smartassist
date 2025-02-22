package com.gowittgroup.smartassist.ui.summary

import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState

data class SummaryUiState(
    val error: String = "",
    val summary: String = "",
    val notificationState: NotificationState? = null,
) : State