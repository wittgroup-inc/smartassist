package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.components.SimpleMarkdown

@Composable
fun SummaryScreen(
    uiState: SummaryUiState,

    onProcessDocuments: (Context, List<Uri>) -> Unit,
    onNotificationClose: () -> Unit = {},
    openDrawer: () -> Unit,
    expandedScreen: Boolean
) {
    val context = LocalContext.current
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
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
            Column(modifier = Modifier.padding(16.dp)) {
                FilePickerScreen { uris ->
                    selectedUris = uris
                }

                Button(onClick = {
                    if (selectedUris.isNotEmpty()) {
                        onProcessDocuments(context, selectedUris)
                    }
                }) {
                    Text("Summarize Documents")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    SimpleMarkdown(uiState.summary)
                }
            }
        }

    })
}