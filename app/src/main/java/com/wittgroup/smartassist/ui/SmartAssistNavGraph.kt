package com.wittgroup.smartassist.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wittgroup.smartassist.AppContainer
import com.wittgroup.smartassist.HomeViewModel


@Composable
fun SmartAssistNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SmartAssistDestinations.SPLASH_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(SmartAssistDestinations.SPLASH_ROUTE) {
            SplashScreen(navController)
        }
        composable(SmartAssistDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.aiDataSource)
            )
            HomeScreen(homeViewModel)
        }
        composable(SmartAssistDestinations.SETTINGS_ROUTE) {

        }
    }
}

