package com.gowittgroup.smartassist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppDrawer
import com.gowittgroup.smartassist.ui.navigation.SmartAssistDestinations
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavGraph
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartAssistApp(
    smartAnalytics: SmartAnalytics,
    widthSizeClass: WindowWidthSizeClass
) {
    SmartAssistTheme {

        val navController = rememberNavController()

        val navigationActions = remember(navController) {
            SmartAssistNavigationActions(navController)
        }

        val coroutineScope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: ""

        val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
        val showAppNavRail = isExpandedScreen &&  currentRoute.isNotBlank() && (currentRoute != SmartAssistDestinations.SPLASH_ROUTE)
        val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)

        ModalNavigationDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToHome = navigationActions.navigateToHome,
                    navigateToHistory = navigationActions.navigateToHistory,
                    navigateToSettings = navigationActions.navigateToSettings,
                    navigateToPrompts = navigationActions.navigateToPrompts,
                    navigateToAbout = navigationActions.navigateToAbout,
                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } }
                )
            },
            drawerState = sizeAwareDrawerState,
            // Only enable opening the drawer via gestures if the screen is not expanded
            gesturesEnabled = !isExpandedScreen
        ) {

            Row {
                if (showAppNavRail) {
                    AppNavRail(
                        currentRoute = currentRoute,
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToHistory = navigationActions.navigateToHistory,
                        navigateToSettings = navigationActions.navigateToSettings,
                        navigateToPrompts = navigationActions.navigateToPrompts,
                        navigateToAbout = navigationActions.navigateToAbout
                    )
                }
                SmartAssistNavGraph(
                    smartAnalytics = smartAnalytics,
                    isExpandedScreen = isExpandedScreen,
                    navController = navController,
                    openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}

@Composable
fun rememberContentPaddingForScreen(
    additionalTop: Dp = 0.dp,
    excludeTop: Boolean = false
) =
    WindowInsets.systemBars
        .only(if (excludeTop) WindowInsetsSides.Bottom else WindowInsetsSides.Vertical)
        .add(WindowInsets(top = additionalTop))
        .asPaddingValues()
