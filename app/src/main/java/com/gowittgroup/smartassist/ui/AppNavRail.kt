package com.gowittgroup.smartassist.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.navigation.SmartAssistDestinations
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@Composable
fun AppNavRail(
    modifier: Modifier = Modifier,
    currentRoute: String,
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    navigateToHistory: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToPrompts: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    NavigationRail(
        header = {
            Icon(
                painterResource(R.drawable.ic_bot_square),
                null,
                Modifier.padding(top = 12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.HOME_ROUTE,
            onClick = { navigateToHome(null, null) },
            icon = { Icon(Icons.Filled.Home, stringResource(R.string.app_name)) },
            label = { Text(stringResource(R.string.home_screen_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.HISTORY_ROUTE,
            onClick = navigateToHistory,
            icon = { Icon(Icons.Filled.History, stringResource(R.string.history_screen_title)) },
            label = { Text(stringResource(R.string.history_screen_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.SETTINGS_ROUTE,
            onClick = navigateToSettings,
            icon = { Icon(Icons.Filled.Settings, stringResource(R.string.settings_screen_title)) },
            label = { Text(stringResource(R.string.settings_screen_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.PROMPTS_ROUTE,
            onClick = navigateToPrompts,
            icon = { Icon(Icons.Filled.QuestionMark, stringResource(R.string.prompts_screen_title)) },
            label = { Text(stringResource(R.string.prompts_screen_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.PROFILE_ROUTE,
            onClick = { navigateToProfile() },
            icon = { Icon(Icons.Filled.Person, stringResource(R.string.app_name)) },
            label = { Text(stringResource(R.string.profile_screen_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == SmartAssistDestinations.ABOUT_ROUTE,
            onClick = navigateToAbout,
            icon = { Icon(Icons.Filled.Info, stringResource(R.string.about_screen_title)) },
            label = { Text(stringResource(R.string.about_screen_title)) },
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
            navigateToHome = { _, _ -> },
            navigateToHistory = {},
            navigateToSettings = {},
            navigateToPrompts = {},
            navigateToAbout = {},
            navigateToProfile = {}
        )
    }
}
