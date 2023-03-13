package com.wittgroup.smartassist.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wittgroup.smartassist.ui.navigation.SmartAssistDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer( currentRoute: String,
               navigateToHome: () -> Unit,
               navigateToSettings: () -> Unit,
               closeDrawer: () -> Unit,
               modifier: Modifier = Modifier){
    ModalDrawerSheet() {
        NavigationDrawerItem(
            label = { Text("Home") },
            icon = { Icon(Icons.Filled.Home, null) },
            selected = currentRoute == SmartAssistDestinations.HOME_ROUTE,
            onClick = { navigateToHome(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("History") },
            icon = { Icon(Icons.Filled.List, null) },
            selected = currentRoute == SmartAssistDestinations.SETTINGS_ROUTE,
            onClick = { navigateToSettings(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

