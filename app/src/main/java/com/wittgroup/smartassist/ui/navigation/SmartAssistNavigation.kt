package com.wittgroup.smartassist.ui.navigation

import androidx.navigation.NavHostController


object SmartAssistDestinations {
    const val SPLASH_ROUTE = "splash"
    const val HOME_ROUTE = "home"
    const val SETTINGS_ROUTE = "settings"
}

class SmartAssistNavigationActions(navController: NavHostController) {
    val navigateToSplash: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SPLASH_ROUTE)
    }
    val navigateToHome: () -> Unit = {
        navController.navigate(SmartAssistDestinations.HOME_ROUTE) {
            popUpTo(0)
        }
    }

    val navigateToSettings: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SETTINGS_ROUTE)
    }
}
