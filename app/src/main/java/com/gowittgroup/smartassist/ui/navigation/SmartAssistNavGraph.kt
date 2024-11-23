package com.gowittgroup.smartassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics


@Composable
fun SmartAssistNavGraph(
    smartAnalytics: SmartAnalytics,
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SmartAssistDestinations.SPLASH_ROUTE
) {
    val navigationActions: SmartAssistNavigationActions =
        remember { SmartAssistNavigationActions(navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        navigation(navigationActions, openDrawer, isExpandedScreen, smartAnalytics)
    }
}

