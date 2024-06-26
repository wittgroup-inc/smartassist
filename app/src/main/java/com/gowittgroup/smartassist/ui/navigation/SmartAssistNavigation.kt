package com.gowittgroup.smartassist.ui.navigation

import androidx.navigation.NavHostController


object SmartAssistDestinations {
    const val SPLASH_ROUTE = "splash"
    const val HOME_ROUTE = "home"
    const val HISTORY_ROUTE = "history"
    const val SETTINGS_ROUTE = "settings"
    const val PROMPTS_ROUTE = "prompt"
    const val ABOUT_ROUTE = "about"
    const val FAQ_ROUTE = "faq"

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
        navController.navigate(SmartAssistDestinations.HISTORY_ROUTE){
            popUpTo(navController.graph.startDestinationId)
        }
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SETTINGS_ROUTE){
            popUpTo(navController.graph.startDestinationId)
        }
    }
    val navigateToPrompts: () -> Unit = {
        navController.navigate(SmartAssistDestinations.PROMPTS_ROUTE){
            popUpTo(navController.graph.startDestinationId)
        }
    }

    val navigateToAbout: () -> Unit = {
        navController.navigate(SmartAssistDestinations.ABOUT_ROUTE){
            popUpTo(navController.graph.startDestinationId)
        }
    }

    val navigateToFaq: () -> Unit = {
        navController.navigate(SmartAssistDestinations.FAQ_ROUTE){
            popUpTo(navController.graph.startDestinationId)
        }
    }

}
