package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.components.SimpleMarkdown
import com.gowittgroup.smartassist.ui.summary.components.ConversationBottomSection
import com.gowittgroup.smartassist.ui.summary.components.DocumentTypeChips
import com.gowittgroup.smartassist.ui.summary.components.FileGridView
import com.gowittgroup.smartassist.ui.summary.models.FileItem

@Composable
fun SummaryScreen(
    uiState: SummaryUiState,
    onGoClick: (Context) -> Unit,
    onNotificationClose: () -> Unit = {},
    openDrawer: () -> Unit,
    expandedScreen: Boolean,
    onSelectFiles: (List<Uri>) -> Unit,
    onRemoveFile: (Uri) -> Unit,
    onTypeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(topBar = {
        when {
            uiState.notificationState != null -> Notification(
                notificationState = uiState.notificationState,
                onNotificationClose = onNotificationClose
            )

            else ->
                AppBar(
                    title = stringResource(R.string.summary_screen_title),
                    openDrawer = openDrawer,
                    isExpanded = expandedScreen
                )
        }
    }, content = { padding ->

        Box(modifier = Modifier.padding(padding)) {
            if (uiState.processingIsInProgress) {
                MovingColorBarLoader()
            }
            Column(modifier = Modifier.padding(16.dp)) {
               Row {
                   FilePickerScreen(modifier = Modifier.weight(1f)) { uris ->
                       onSelectFiles(uris)
                   }

                   Spacer(modifier = Modifier.height(24.dp))

                   Button(onClick = {
                       if (uiState.selectedFiles.isNotEmpty()) {
                           onGoClick(context)
                       }
                   }) {
                       Text("Go")
                   }
               }
                DocumentTypeChips(
                    onTypeSelected = { type -> onTypeSelected(type) },
                    selectedType = uiState.documentType
                )

                FileGridView(uiState.selectedFiles.map { FileItem(it.toString(), it) }) {
                    onRemoveFile(it.thumbnailUrl)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    SimpleMarkdown(uiState.summary)
                    if(uiState.summary.isNotEmpty()){
                        ConversationBottomSection(item = uiState.summary, context = context)
                    }
                }
            }
        }

    })
}


