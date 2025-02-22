package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import com.gowittgroup.smartassist.core.Intent

sealed class SummaryIntent : Intent {
    data class FilesSelected(val selectedFiles: List<Uri>) : SummaryIntent()
    data class ProcessFiles(val context: Context) : SummaryIntent()
    data class RemoveFileFromList(val uri: Uri) : SummaryIntent()
    data class DocumentTypeSelected(val type: String) : SummaryIntent()
}