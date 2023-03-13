package com.wittgroup.smartassist.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.navigation.SmartAssistDestinations

@Composable
fun AppNavRail(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        header = {
            Icon(
                painterResource(R.drawable.ic_bot),
                null,
                Modifier.padding(vertical = 12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.HOME_ROUTE,
            onClick = navigateToHome,
            icon = { Icon(Icons.Filled.Home, "Smart Assist") },
            label = { Text("SmartAssist") },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.SETTINGS_ROUTE,
            onClick = navigateToSettings,
            icon = { Icon(Icons.Filled.List, "Settings") },
            label = { Text("Settings") },
            alwaysShowLabel = false
        )
        Spacer(Modifier.weight(1f))
    }
}
