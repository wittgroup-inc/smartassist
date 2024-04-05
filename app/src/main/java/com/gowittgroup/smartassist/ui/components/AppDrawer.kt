package com.gowittgroup.smartassist.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
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
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    navigateToHistory: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToPrompts: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet() {
        SmartAssistLogo(
            modifier = Modifier.padding(vertical = 16.dp)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.home_screen_title)) },
            icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.home_screen_title)) },
            selected = currentRoute == SmartAssistDestinations.HOME_ROUTE,
            onClick = { navigateToHome(null, null); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.history_screen_title)) },
            icon = { Icon(Icons.Filled.History, contentDescription = stringResource(R.string.history_screen_title)) },
            selected = currentRoute == SmartAssistDestinations.HISTORY_ROUTE,
            onClick = { navigateToHistory(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.settings_screen_title)) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_screen_title)) },
            selected = currentRoute == SmartAssistDestinations.SETTINGS_ROUTE,
            onClick = { navigateToSettings(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            label = { Text(stringResource(R.string.prompts_screen_title)) },
            icon = { Icon(Icons.Filled.QuestionMark, contentDescription = stringResource(R.string.prompts_screen_title)) },
            selected = currentRoute == SmartAssistDestinations.PROMPTS_ROUTE,
            onClick = { navigateToPrompts(); closeDrawer() },
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
            contentDescription = stringResource(id = R.string.logo_content_desc),
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
            navigateToHome = { _, _ ->},
            navigateToHistory = {},
            navigateToSettings = {},
            navigateToPrompts = {},
            closeDrawer = { }
        )
    }
}
