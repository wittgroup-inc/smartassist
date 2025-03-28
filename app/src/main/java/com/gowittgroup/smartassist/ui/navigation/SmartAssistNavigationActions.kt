package com.gowittgroup.smartassist.ui.navigation

import androidx.navigation.NavHostController


class SmartAssistNavigationActions(navController: NavHostController) {
    val navigateToSplash: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SPLASH_ROUTE)
    }
    val navigateToSignIn: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SIGN_IN_ROUTE) {
            popUpTo(0)
        }
    }
    val navigateToSignUp: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SIGN_UP_ROUTE) {
            popUpTo(0)
        }
    }
    val navigateToHome: (id: Long?, prompt: String?) -> Unit = { id, prompt ->
        val param1 = id?.let { it -> "/$it" } ?: "/-1"
        val param2 = prompt?.let { it -> "/$it" } ?: "/none"
        navController.navigate(SmartAssistDestinations.HOME_ROUTE + param1 + param2) {
            popUpTo(0)
        }
    }
    val navigateToHistory: () -> Unit = {
        navController.navigate(SmartAssistDestinations.HISTORY_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }
    val navigateToSummarize: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SUMMARIZE_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SETTINGS_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }
    val navigateToPrompts: () -> Unit = {
        navController.navigate(SmartAssistDestinations.PROMPTS_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }

    val navigateToAbout: () -> Unit = {
        navController.navigate(SmartAssistDestinations.ABOUT_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }

    val navigateToFaq: () -> Unit = {
        navController.navigate(SmartAssistDestinations.FAQ_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }

    val navigateToSubscription: () -> Unit = {
        navController.navigate(SmartAssistDestinations.SUBSCRIPTION_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }

    val navigateToProfile: () -> Unit = {
        navController.navigate(SmartAssistDestinations.PROFILE_ROUTE) {
            popUpTo(navController.graph.startDestinationId)
        }
    }
}
