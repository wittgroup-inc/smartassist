@file:JvmName("SummaryScreen2Kt")

package com.gowittgroup.smartassist.ui.summary

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.components.SimpleMarkdown
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.SecondaryOutlinedButton
import com.gowittgroup.smartassist.ui.summary.components.ConversationBottomSection
import com.gowittgroup.smartassist.ui.summary.components.DocumentTypeChips
import com.gowittgroup.smartassist.ui.summary.components.FileGridView
import com.gowittgroup.smartassist.ui.summary.components.SelectSourceBottomSheet
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
    var showBottomSheet by remember { mutableStateOf(false) }

    val galleryLauncher = rememberGalleryLauncher(onSelectFiles)

    val scanLauncher = rememberScannerLauncher(scannedImages = onSelectFiles)
    val scanner = rememberDocumentScanner()

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
            SummaryScreenContent(
                scrollState = scrollState,
                showBottomSheet = { showBottomSheet = it },
                onTypeSelected = onTypeSelected,
                uiState = uiState,
                onRemoveFile = onRemoveFile,
                onGoClick = onGoClick,
                context = context
            )
        }

    })

    if (showBottomSheet) {
        SelectSourceBottomSheet(
            onDismiss = { showBottomSheet = false },
            onScan = {
                scanner.getStartScanIntent(context as Activity)
                    .addOnSuccessListener { intentSender ->
                        scanLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                    }
                    .addOnFailureListener { e ->
                        Log.e("Scanner Error", "Failed to start scanner: ${e.message}")
                    }
                showBottomSheet = false
            },
            onGallery = {
                galleryLauncher.launch(arrayOf("application/pdf", "image/*"))
                showBottomSheet = false
            }
        )
    }
}

@Composable
private fun SummaryScreenContent(
    scrollState: ScrollState,
    showBottomSheet: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit,
    uiState: SummaryUiState,
    onRemoveFile: (Uri) -> Unit,
    onGoClick: (Context) -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        SecondaryOutlinedButton(
            onClick = {
                showBottomSheet(true)
            },
            modifier = Modifier.fillMaxWidth(),
            text = "Select Documents"
        )
        Spacer(modifier = Modifier.height(16.dp))
        DocumentTypeChips(
            onTypeSelected = { type -> onTypeSelected(type) },
            selectedType = uiState.documentType
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.selectedFiles.isNotEmpty()) {
            FileGridView(uiState.selectedFiles.map { FileItem(it.toString(), it) }) {
                onRemoveFile(it.thumbnailUrl)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        PrimaryButton(
            onClick = {
                if (uiState.selectedFiles.isNotEmpty()) {
                    onGoClick(context)
                }

            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.summarizedButtonEnabled,
            text = "Summarize"
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column() {
            SimpleMarkdown(uiState.summary)
            if (uiState.showSummaryFooter) {
                ConversationBottomSection(item = uiState.summary, context = context)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SummaryScreenPreview() {
    val dummyUiState = SummaryUiState(
        selectedFiles = listOf(
            Uri.parse("content://dummy/file1.jpg"),
            Uri.parse("content://dummy/file2.pdf")
        ),
        documentType = "PDF",
        processingIsInProgress = false,
        notificationState = null
    )

    SummaryScreenContent(
        uiState = dummyUiState,
        onGoClick = {},
        onRemoveFile = { /* Mock function */ },
        onTypeSelected = { /* Mock function */ },
        scrollState = rememberScrollState(),
        showBottomSheet = {},
        context = LocalContext.current
    )
}


