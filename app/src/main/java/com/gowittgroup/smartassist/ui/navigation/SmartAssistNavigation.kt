package com.gowittgroup.smartassist.ui.navigation

import androidx.navigation.NavHostController


object SmartAssistDestinations {
    const val SPLASH_ROUTE = "splash"
    const val HOME_ROUTE = "home"
    const val HISTORY_ROUTE = "history"
    const val SETTINGS_ROUTE = "settings"
    const val PROMPTS_ROUTE = "prompt"
}

class SmartAssistNavigationActions(navController: NavHostController) {
    val navigateToSplash: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SPLASH_ROUTE)
    }
    val navigateToHome: (id: Long?, prompt: String?) -> Unit = { id, prompt ->
        val param1 = id?.let { it -> "/$it" } ?: "/-1"
        val param2 = prompt?.let { it -> "/$it" } ?: "/none"
        navController.navigate(SmartAssistDestinations.HOME_ROUTE + param1 + param2) {
            popUpTo(0)
        }
    }
    val navigateToHistory: () -> Unit = {
        navController.navigate(SmartAssistDestinations.HISTORY_ROUTE)
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SETTINGS_ROUTE)
    }
    val navigateToPrompts: () -> Unit = {
        navController.navigate(SmartAssistDestinations.PROMPTS_ROUTE)
    }

}
