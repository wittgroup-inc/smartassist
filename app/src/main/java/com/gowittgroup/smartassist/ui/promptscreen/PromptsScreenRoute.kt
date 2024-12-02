package com.gowittgroup.smartassist.ui.promptscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun PromptsScreenRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    navigationActions: SmartAssistNavigationActions,
    smartAnalytics: SmartAnalytics
) {
    val promptsViewModel: PromptsViewModel = hiltViewModel()
    val uiState by promptsViewModel.uiState.collectAsState()
    PromptsScreen(
        uiState = uiState,
        isExpanded = isExpandedScreen,
        openDrawer = openDrawer,
        navigateToHome = navigationActions.navigateToHome,
        smartAnalytics = smartAnalytics,
        onNotificationClose = promptsViewModel::clearNotification
    )
}