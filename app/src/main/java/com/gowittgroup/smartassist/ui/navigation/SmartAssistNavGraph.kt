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
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreen
import com.gowittgroup.smartassist.ui.promptscreen.PromptsViewModel
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
        composable(route = SmartAssistDestinations.HOME_ROUTE + "/{id}/{prompt}", arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            },
            navArgument("prompt") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            }
        )) { navBackStack ->
            val id = navBackStack.arguments?.getString("id")
            val prompt = navBackStack.arguments?.getString("prompt")
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    appContainer.answerRepository,
                    appContainer.settingsRepository,
                    appContainer.conversationHistoryRepository,
                    id?.toLong(),
                    prompt,
                    appContainer.networkUtil,
                    appContainer.homeScreenTranslations
                )
            )
            HomeScreen(
                viewModel = homeViewModel,
                openDrawer = openDrawer,
                showTopAppBar = !isExpandedScreen,
                isExpanded = isExpandedScreen,
                navigateToSettings = navigationActions.navigateToSettings,
                navigateToHistory = navigationActions.navigateToHistory,
                navigateToPrompts = navigationActions.navigateToPrompts,
                navigateToHome = navigationActions.navigateToHome,
                smartAnalytics = appContainer.smartAnalytics
            )
        }
        composable(SmartAssistDestinations.HISTORY_ROUTE) {
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModel.provideFactory(appContainer.conversationHistoryRepository)
            )
            HistoryScreen(
                viewModel = historyViewModel,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                navigateToHome = navigationActions.navigateToHome,
                smartAnalytics = appContainer.smartAnalytics
            )
        }
        composable(SmartAssistDestinations.SETTINGS_ROUTE) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.provideFactory(
                    appContainer.settingsRepository,
                    appContainer.networkUtil,
                    appContainer.settingScreenTranslations
                )
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                smartAnalytics = appContainer.smartAnalytics
            )
        }

        composable(SmartAssistDestinations.PROMPTS_ROUTE) {
            val promptsViewModel: PromptsViewModel = viewModel(
                factory = PromptsViewModel.provideFactory(
                    appContainer.promptsRepository,
                    appContainer.networkUtil,
                    appContainer.promptsScreenTranslations
                )
            )
            PromptsScreen(
                viewModel = promptsViewModel,
                isExpanded = isExpandedScreen,
                openDrawer = openDrawer,
                navigateToHome =  navigationActions.navigateToHome,
                smartAnalytics = appContainer.smartAnalytics,
            )
        }
    }
}

