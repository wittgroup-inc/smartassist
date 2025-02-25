@file:JvmName("SummaryScreen2Kt")

package com.gowittgroup.smartassist.ui.summary

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.summary.components.SelectSourceBottomSheet
import com.gowittgroup.smartassist.ui.summary.components.SummaryScreenContent

@Composable
fun SummaryScreen(
    uiState: SummaryUiState,
    onGoClick: (Context) -> Unit,
    onNotificationClose: () -> Unit = {},
    openDrawer: () -> Unit,
    expandedScreen: Boolean,
    onSelectFiles: (List<Uri>) -> Unit,
    onRemoveFile: (Uri) -> Unit,
    onTypeSelected: (String) -> Unit,
    onInit: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val galleryLauncher =
        rememberGalleryLauncher(onSelectFiles = onSelectFiles, limit = uiState.maxDocumentLimit)

    val scanLauncher = rememberScannerLauncher(scannedImages = onSelectFiles)
    val scanner = rememberDocumentScanner(limit = uiState.maxDocumentLimit)

    LaunchedEffect(Unit) {
        onInit()
    }

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
                context = context,
                expandedScreen = expandedScreen
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
                        SmartLog.e("Scanner Error", "Failed to start scanner: ${e.message}")
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


