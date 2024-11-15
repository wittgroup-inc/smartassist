package com.gowittgroup.smartassist.ui.aboutscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun AboutScreenRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics,
    navigationActions: SmartAssistNavigationActions
) {
    val aboutViewModel: AboutViewModel = hiltViewModel()
    val uiState by aboutViewModel.uiState.collectAsState()
    AboutScreen(
        uiState = uiState,
        isExpanded = isExpandedScreen,
        openDrawer = openDrawer,
        smartAnalytics = smartAnalytics,
        refreshErrorMessage = aboutViewModel::resetErrorMessage,
        navigateToFaq = navigationActions.navigateToFaq
    )
}