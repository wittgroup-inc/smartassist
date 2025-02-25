package com.gowittgroup.smartassist.ui.summary

import android.net.Uri
import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState

data class SummaryUiState(
    val error: String = "",
    val summary: String = "",
    val selectedFiles: List<Uri> = emptyList(),
    val notificationState: NotificationState? = null,
    val processingIsInProgress: Boolean = false,
    val documentType: String = "",
    val showSummaryFooter: Boolean = false,
    val maxDocumentLimit: Int = 2,
    val aiTool: String = ""
) : State {
    val summarizedButtonEnabled: Boolean
        get() = selectedFiles.isNotEmpty()
}