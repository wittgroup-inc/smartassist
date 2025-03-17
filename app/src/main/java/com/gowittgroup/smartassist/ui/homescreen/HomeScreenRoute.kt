package com.gowittgroup.smartassist.ui.homescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun HomeScreenRoute(
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    navigationActions: SmartAssistNavigationActions,
    smartAnalytics: SmartAnalytics
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        openDrawer = openDrawer,
        showTopAppBar = !isExpandedScreen,
        isExpanded = isExpandedScreen,
        navigateToSettings = navigationActions.navigateToSettings,
        navigateToHistory = navigationActions.navigateToHistory,
        navigateToPrompts = navigationActions.navigateToPrompts,
        navigateToHome = navigationActions.navigateToHome,
        navigateToSubscription = navigationActions.navigateToSubscription,
        navigateToSummarize = navigationActions.navigateToSummarize,
        smartAnalytics = smartAnalytics,
        ask = homeViewModel::ask,
        beginningSpeech = homeViewModel::beginningSpeech,
        setCommandModeAfterReply = homeViewModel::setCommandModeAfterReply,
        handsFreeModeStopListening = homeViewModel::handsFreeModeStopListening,
        setCommandMode = homeViewModel::setCommandMode,
        releaseCommandMode = homeViewModel::releaseCommandMode,
        handsFreeModeStartListening = homeViewModel::handsFreeModeStartListening,
        resetErrorMessage = homeViewModel::resetErrorMessage,
        setReadAloud = homeViewModel::setReadAloud,
        closeHandsFreeAlert = homeViewModel::closeHandsFreeAlert,
        setHandsFreeMode = homeViewModel::setHandsFreeMode,
        stopListening = homeViewModel::stopListening,
        startListening = homeViewModel::startListening,
        updateHint = homeViewModel::updateHint,
        refreshAll = homeViewModel::refreshAll
    )
}
