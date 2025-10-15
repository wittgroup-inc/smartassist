package com.gowittgroup.smartassist.ui.faqscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics

@Composable
fun FaqScreenRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics
) {
    val faqViewModel: FaqViewModel = hiltViewModel()
    val uiState by faqViewModel.uiState.collectAsState()
    FaqScreen(
        uiState = uiState,
        isExpanded = isExpandedScreen,
        openDrawer = openDrawer,
        smartAnalytics = smartAnalytics
    )
}