package com.gowittgroup.smartassist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gowittgroup.smartassist.ui.aboutscreen.AboutScreenRoute
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.auth.signin.SignInScreenRoute
import com.gowittgroup.smartassist.ui.auth.signup.SignUpScreenRoute
import com.gowittgroup.smartassist.ui.faqscreen.FaqScreenRoute
import com.gowittgroup.smartassist.ui.history.HistoryScreenRoute
import com.gowittgroup.smartassist.ui.homescreen.HomeScreenRoute
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreenRoute
import com.gowittgroup.smartassist.ui.settingsscreen.SettingsScreenRoute
import com.gowittgroup.smartassist.ui.splashscreen.SplashScreen
import com.gowittgroup.smartassist.ui.subscription.SubscriptionScreenRoute


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

        composable(SmartAssistDestinations.SPLASH_ROUTE) {
            SplashScreen(
                navigateToHome = navigationActions.navigateToHome,
                navigateToSignUp = navigationActions.navigateToSignUp,
                navigateToSignIn = navigationActions.navigateToSignIn
            )
        }

        composable(SmartAssistDestinations.SIGN_IN) {
            SignInScreenRoute(navigationActions)
        }

        composable(SmartAssistDestinations.SIGN_UP) {
            SignUpScreenRoute(navigationActions)
        }

        composable(
            route = SmartAssistDestinations.HOME_ROUTE + "/{id}/{prompt}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            }, navArgument("prompt") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {
            HomeScreenRoute(openDrawer, isExpandedScreen, navigationActions, smartAnalytics)
        }
        composable(SmartAssistDestinations.HISTORY_ROUTE) {
            HistoryScreenRoute(isExpandedScreen, openDrawer, navigationActions, smartAnalytics)
        }
        composable(SmartAssistDestinations.SETTINGS_ROUTE) {
            SettingsScreenRoute(
                navigationActions,
                isExpandedScreen,
                openDrawer,
                smartAnalytics
            )
        }

        composable(SmartAssistDestinations.PROMPTS_ROUTE) {
            PromptsScreenRoute(isExpandedScreen, openDrawer, navigationActions, smartAnalytics)
        }

        composable(SmartAssistDestinations.ABOUT_ROUTE) {
            AboutScreenRoute(isExpandedScreen, openDrawer, smartAnalytics, navigationActions)
        }

        composable(SmartAssistDestinations.FAQ_ROUTE) {
            FaqScreenRoute(isExpandedScreen, openDrawer, smartAnalytics)
        }

        composable(SmartAssistDestinations.SUBSCRIPTION) {
            SubscriptionScreenRoute(isExpandedScreen, openDrawer, smartAnalytics)
        }
    }
}











