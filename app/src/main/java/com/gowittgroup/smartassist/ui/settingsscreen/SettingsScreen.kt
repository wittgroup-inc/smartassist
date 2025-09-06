package com.gowittgroup.smartassist.ui.settingsscreen

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.settingsscreen.components.Spinner
import com.gowittgroup.smartassist.ui.settingsscreen.components.ToggleSetting
import com.gowittgroup.smartassistlib.models.ai.AiTools

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics,
    onNotificationClose: () -> Unit,
    toggleReadAloud: (isOn: Boolean) -> Unit,
    toggleHandsFreeMode: (isOn: Boolean) -> Unit,
    chooseAiTool: (aiTool: AiTools) -> Unit,
    chooseChatModel: (chatModel: String) -> Unit,
    onLogout: (Context) -> Unit,
    onDeleteAccount: () -> Unit,
    navigateToSubscription: () -> Unit,
) {

    val context = LocalContext.current

    logUserEntersEvent(smartAnalytics)

    Scaffold(topBar = {
        when {
            uiState.notificationState != null ->
                Notification(uiState.notificationState, onNotificationClose)

            else ->
                AppBar(
                    title = stringResource(R.string.settings_screen_title),
                    openDrawer = openDrawer,
                    isExpanded = isExpanded
                )
        }
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                ToggleSetting(
                    title = stringResource(R.string.read_aloud_label),
                    isChecked = uiState.readAloud
                ) {
                    toggleReadAloud(it)
                }
                HorizontalDivider()
                ToggleSetting(
                    title = stringResource(R.string.hands_free_mode_label),
                    isChecked = uiState.handsFreeMode
                ) {
                    toggleHandsFreeMode(it)
                }
                HorizontalDivider()
                Spinner(
                    items = uiState.tools.filter { it != AiTools.NONE }
                        .map { SpinnerItem(it, it.displayName) },
                    selectedItem = SpinnerItem(
                        uiState.selectedAiTool,
                        uiState.selectedAiTool.displayName
                    ),
                    toolTip = stringResource(R.string.ai_tools_tool_tips)
                ) {
                    chooseAiTool(it)
                }
                HorizontalDivider()
                Spinner(
                    items = uiState.models.map { SpinnerItem(it.name, it.name, it.isActive) },
                    selectedItem = SpinnerItem(
                        uiState.selectedAiModel,
                        uiState.selectedAiModel
                    ),
                    toolTip = stringResource(R.string.ai_models_tool_tips)
                ) {
                    chooseChatModel(it)
                }
                HorizontalDivider()
                Text(
                    text = stringResource(id = R.string.subscription_screen_title),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable(onClick = navigateToSubscription)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                HorizontalDivider()
                Text(
                    text = stringResource(id = R.string.logout),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable(onClick = { onLogout(context) })
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                HorizontalDivider()
                Text(
                    text = stringResource(id = R.string.delete_account),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .clickable(onClick = onDeleteAccount)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                HorizontalDivider()
            }
        }

    }

    )
}


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "settings_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}

data class SpinnerItem<T>(val data: T, val displayName: String, val isEnabled: Boolean = true)


@Preview
@Composable
fun SettingScreenPreview() {
    SettingsScreen(
        uiState = SettingsUiState(),
        isExpanded = false,
        openDrawer = { },
        smartAnalytics = FakeAnalytics(),
        onNotificationClose = { },
        toggleReadAloud = {},
        toggleHandsFreeMode = {},
        chooseAiTool = {},
        chooseChatModel = {},
        onLogout = {},
        onDeleteAccount = {},
        navigateToSubscription = {}
    )
}