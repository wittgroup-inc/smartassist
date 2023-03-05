package com.wittgroup.smartassist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wittgroup.smartassist.ui.SmartAssistDestinations
import com.wittgroup.smartassist.ui.SmartAssistNavGraph
import com.wittgroup.smartassist.ui.theme.SmartAssistTheme

@Composable
fun SmartAssistApp(
    appContainer: AppContainer
) {
    SmartAssistTheme {

        val navController = rememberNavController()

//        val navigationActions = remember(navController) {
//            SmartAssistNavigationActions(navController)
//        }

        val coroutineScope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: SmartAssistDestinations.HOME_ROUTE


        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            SmartAssistNavGraph(
                appContainer = appContainer,
                navController = navController
            )
        }
    }
}
