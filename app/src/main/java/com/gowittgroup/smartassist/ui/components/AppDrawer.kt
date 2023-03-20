package com.gowittgroup.smartassist.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.navigation.SmartAssistDestinations
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: (id: Long?) -> Unit,
    navigateToHistory: () -> Unit,
    navigateToSettings: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet() {
        SmartAssistLogo(
            modifier = Modifier.padding(vertical = 16.dp)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.home)) },
            icon = { Icon(Icons.Filled.Home, null) },
            selected = currentRoute == SmartAssistDestinations.HOME_ROUTE,
            onClick = { navigateToHome(-1); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.history_screen_title)) },
            icon = { Icon(Icons.Filled.History, null) },
            selected = currentRoute == SmartAssistDestinations.HISTORY_ROUTE,
            onClick = { navigateToHistory(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.settings_screen_title)) },
            icon = { Icon(Icons.Filled.Settings, null) },
            selected = currentRoute == SmartAssistDestinations.SETTINGS_ROUTE,
            onClick = { navigateToSettings(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun SmartAssistLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val logo = painterResource(id = R.drawable.ic_bot_square)
        val logoAspectRatio = logo.intrinsicSize.width / logo.intrinsicSize.height
        Icon(
            painterResource(R.drawable.ic_bot_square),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 28.dp)
                .height(32.dp)
                .aspectRatio(logoAspectRatio)
        )

        val title = painterResource(id = R.drawable.ic_app_title)
        val titleAspectRatio = title.intrinsicSize.width / title.intrinsicSize.height
        Icon(
            painter = painterResource(R.drawable.ic_app_title),
            modifier = modifier
                .padding(start = 8.dp)
                .height(32.dp)
                .aspectRatio(titleAspectRatio),
            contentDescription = stringResource(R.string.app_name),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    SmartAssistTheme {
        AppDrawer(
            currentRoute = SmartAssistDestinations.HOME_ROUTE,
            navigateToHome = {},
            navigateToHistory = {},
            navigateToSettings = {},
            closeDrawer = { }
        )
    }
}
