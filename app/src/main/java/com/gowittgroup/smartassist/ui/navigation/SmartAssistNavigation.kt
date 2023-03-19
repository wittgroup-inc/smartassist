package com.gowittgroup.smartassist.ui.navigation

import androidx.navigation.NavHostController


object SmartAssistDestinations {
    const val SPLASH_ROUTE = "splash"
    const val HOME_ROUTE = "home"
    const val HISTORY_ROUTE = "history"
    const val SETTINGS_ROUTE = "settings"
}

class SmartAssistNavigationActions(navController: NavHostController) {
    val navigateToSplash: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SPLASH_ROUTE)
    }
    val navigateToHome: (id: Long?) -> Unit = { it ->
        val param = it?.let { id -> "/$id" } ?: ""
        navController.navigate(SmartAssistDestinations.HOME_ROUTE + param) {
            popUpTo(0)
        }
    }
    val navigateToHistory: () -> Unit = {
        navController.navigate(SmartAssistDestinations.HISTORY_ROUTE)
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SETTINGS_ROUTE)
    }

}
