package com.gowittgroup.smartassist.ui.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun SummaryScreenRoute(
    expandedScreen: Boolean,
    openDrawer: () -> Unit,
    navigationActions: SmartAssistNavigationActions
) {
    val summaryViewModel: SummaryViewModel = hiltViewModel()
    val uiState by summaryViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        summaryViewModel.sideEffects.collect { sideEffect ->

        }
    }

    SummaryScreen(
        uiState = uiState,
        onGoClick = { summaryViewModel.processIntent(SummaryIntent.ProcessFiles(it)) },
        openDrawer = openDrawer,
        expandedScreen = expandedScreen,
        onSelectFiles = { summaryViewModel.processIntent(SummaryIntent.FilesSelected(it)) },
        onRemoveFile = {summaryViewModel.processIntent(SummaryIntent.RemoveFileFromList(it))}
    )
}