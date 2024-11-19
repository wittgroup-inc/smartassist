package com.gowittgroup.smartassist.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gowittgroup.smartassist.ui.aboutscreen.AboutScreenRoute
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.auth.signin.SignInScreenRoute
import com.gowittgroup.smartassist.ui.auth.signup.SignUpScreenRoute
import com.gowittgroup.smartassist.ui.faqscreen.FaqScreenRoute
import com.gowittgroup.smartassist.ui.history.HistoryScreenRoute
import com.gowittgroup.smartassist.ui.homescreen.HomeScreenRoute
import com.gowittgroup.smartassist.ui.profile.ProfileScreenRoute
import com.gowittgroup.smartassist.ui.promptscreen.PromptsScreenRoute
import com.gowittgroup.smartassist.ui.settingsscreen.SettingsScreenRoute
import com.gowittgroup.smartassist.ui.splashscreen.SplashScreen
import com.gowittgroup.smartassist.ui.subscription.SubscriptionScreenRoute

internal fun NavGraphBuilder.navigation(
    navigationActions: SmartAssistNavigationActions,
    openDrawer: () -> Unit,
    isExpandedScreen: Boolean,
    smartAnalytics: SmartAnalytics
) {
    composable(SmartAssistDestinations.SPLASH_ROUTE) {
        SplashScreen(
            navigateToHome = navigationActions.navigateToHome,
            navigateToSignUp = navigationActions.navigateToSignUp,
            navigateToSignIn = navigationActions.navigateToSignIn
        )
    }

    composable(SmartAssistDestinations.SIGN_IN_ROUTE) {
        SignInScreenRoute(navigationActions)
    }

    composable(SmartAssistDestinations.SIGN_UP_ROUTE) {
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

    composable(SmartAssistDestinations.SUBSCRIPTION_ROUTE) {
        SubscriptionScreenRoute(isExpandedScreen, openDrawer, smartAnalytics)
    }

    composable(SmartAssistDestinations.PROFILE_ROUTE) {
        ProfileScreenRoute(isExpandedScreen, openDrawer, navigationActions)
    }
}