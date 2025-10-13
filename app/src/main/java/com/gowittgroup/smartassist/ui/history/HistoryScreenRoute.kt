package com.gowittgroup.smartassist.ui.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun HistoryScreenRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    navigationActions: SmartAssistNavigationActions,
    smartAnalytics: SmartAnalytics
) {
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val uiState by historyViewModel.uiState.collectAsState()
    HistoryScreen(
        uiState = uiState,
        isExpanded = isExpandedScreen,
        openDrawer = openDrawer,
        navigateToHome = navigationActions.navigateToHome,
        smartAnalytics = smartAnalytics,
        deleteHistory = historyViewModel::deleteHistory,
        onQueryChange = historyViewModel::search
    )
}