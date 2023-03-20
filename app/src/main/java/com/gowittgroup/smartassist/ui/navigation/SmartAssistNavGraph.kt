package com.gowittgroup.smartassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gowittgroup.smartassist.AppContainer
import com.gowittgroup.smartassist.ui.history.HistoryScreen
import com.gowittgroup.smartassist.ui.history.HistoryViewModel
import com.gowittgroup.smartassist.ui.homescreen.HomeScreen
import com.gowittgroup.smartassist.ui.homescreen.HomeViewModel
import com.gowittgroup.smartassist.ui.settingsscreen.SettingsScreen
import com.gowittgroup.smartassist.ui.settingsscreen.SettingsViewModel
import com.gowittgroup.smartassist.ui.splashscreen.SplashScreen


@Composable
fun SmartAssistNavGraph(
    appContainer: AppContainer,
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SmartAssistDestinations.SPLASH_ROUTE
) {
    val navigationActions: SmartAssistNavigationActions = remember { SmartAssistNavigationActions(navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(SmartAssistDestinations.SPLASH_ROUTE) {
            SplashScreen(navigationActions.navigateToHome)
        }
        composable(route = SmartAssistDestinations.HOME_ROUTE + "/{id}", arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
                defaultValue = "-1"
                nullable = true
            }
        )) { navBackStack ->
            val id = navBackStack.arguments?.getString("id")
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    appContainer.answerRepository,
                    appContainer.settingsRepository,
                    appContainer.conversationHistoryRepository,
                    id
                )
            )
            HomeScreen(
                viewModel = homeViewModel,
                openDrawer = openDrawer,
                showTopAppBar = !isExpandedScreen,
                isExpanded = isExpandedScreen,
                navigateToSettings = navigationActions.navigateToSettings,
                navigateToHistory = navigationActions.navigateToHistory,
                navigateToHome = navigationActions.navigateToHome
            )
        }
        composable(SmartAssistDestinations.HISTORY_ROUTE) {
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModel.provideFactory(appContainer.conversationHistoryRepository)
            )
            HistoryScreen(viewModel = historyViewModel, isExpanded = isExpandedScreen, openDrawer = openDrawer, navigationActions.navigateToHome)
        }
        composable(SmartAssistDestinations.SETTINGS_ROUTE) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.provideFactory(appContainer.settingsRepository)
            )
            SettingsScreen(viewModel = settingsViewModel, isExpanded = isExpandedScreen, openDrawer = openDrawer)
        }
    }
}

