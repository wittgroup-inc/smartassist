package com.wittgroup.smartassist.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.navigation.SmartAssistDestinations
import com.wittgroup.smartassist.ui.theme.SmartAssistTheme

@Composable
fun AppNavRail(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        header = {
            Icon(
                painterResource(R.drawable.ic_bot_square),
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
            label = { Text("Home") },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.HISTORY_ROUTE,
            onClick = navigateToHistory,
            icon = { Icon(Icons.Filled.History, "History") },
            label = { Text("History") },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.SETTINGS_ROUTE,
            onClick = navigateToSettings,
            icon = { Icon(Icons.Filled.Settings, "History") },
            label = { Text("History") },
            alwaysShowLabel = false
        )
        Spacer(Modifier.weight(1f))
    }
}


@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRail() {
    SmartAssistTheme() {
        AppNavRail(
            currentRoute = SmartAssistDestinations.HOME_ROUTE,
            navigateToHome = {},
            navigateToHistory = {},
            navigateToSettings = {}
        )
    }
}
