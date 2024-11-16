package com.gowittgroup.smartassist.ui.settingsscreen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun SettingsScreenRoute(
    navigationActions: SmartAssistNavigationActions,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val uiState by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        settingsViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is SettingsSideEffects.ShowToast -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }

                is SettingsSideEffects.SignOut -> {
                    // Handle navigation action here
                    navigationActions.navigateToSignIn()
                }
            }
        }
    }
    SettingsScreen(
        uiState = uiState,
        isExpanded = isExpandedScreen,
        openDrawer = openDrawer,
        smartAnalytics = smartAnalytics,
        refreshErrorMessage = settingsViewModel::resetErrorMessage,
        toggleReadAloud = settingsViewModel::toggleReadAloud,
        toggleHandsFreeMode = settingsViewModel::toggleHandsFreeMode,
        chooseAiTool = settingsViewModel::chooseAiTool,
        chooseChatModel = settingsViewModel::chooseChatModel,
        onLogout = settingsViewModel::logout,
        navigateToSubscription = navigationActions.navigateToSubscription,
        onDeleteAccount = settingsViewModel::onDeleteAccount
    )
}