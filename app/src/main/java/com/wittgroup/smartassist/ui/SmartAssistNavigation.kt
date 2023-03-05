package com.wittgroup.smartassist.ui

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


object SmartAssistDestinations {
    const val SPLASH_ROUTE = "splash"
    const val HOME_ROUTE = "home"
    const val SETTINGS_ROUTE = "settings"
}

//class SmartAssistNavigationActions(navController: NavHostController) {
//    val navigateToSplash: () -> Unit = {
//        navController.navigate(SmartAssistDestinations.SPLASH_ROUTE)
//    }
//    val navigateToHome: () -> Unit = {
//
//        navController.navigate(SmartAssistDestinations.HOME_ROUTE)
//
//    }
//
//    val navigateToSettings: () -> Unit = {
//        navController.navigate(SmartAssistDestinations.SETTINGS_ROUTE) {
//            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
//            }
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//}
