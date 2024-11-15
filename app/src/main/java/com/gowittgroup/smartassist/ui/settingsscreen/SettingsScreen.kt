package com.gowittgroup.smartassist.ui.settingsscreen

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.User
import org.checkerframework.checker.units.qual.Current

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics,
    refreshErrorMessage: () -> Unit,
    toggleReadAloud: (isOn: Boolean) -> Unit,
    toggleHandsFreeMode: (isOn: Boolean) -> Unit,
    chooseAiTool: (aiTool: AiTools) -> Unit,
    chooseChatModel: (chatModel: String) -> Unit,
    onLogout: () -> Unit
) {

    logUserEntersEvent(smartAnalytics)

    ErrorView(uiState.error).also { refreshErrorMessage() }

    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.settings_screen_title),
            openDrawer = openDrawer,
            isExpanded = isExpanded
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            Column(modifier = Modifier.padding(padding)) {

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
                    toolTip = "Switch AI tools for better result."
                ) {
                    chooseAiTool(it)
                }
                HorizontalDivider()
                Spinner(
                    items = uiState.models.map { SpinnerItem(it, it) },
                    selectedItem = SpinnerItem(
                        uiState.selectedAiModel,
                        uiState.selectedAiModel
                    ),
                    toolTip = "All models might not work. Select GPT-x.x..  should work perfectly."
                ) {
                    chooseChatModel(it)
                }
                HorizontalDivider()
                Text(
                    text = "UUID: ${uiState.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                HorizontalDivider()
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable(onClick = onLogout)
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

@Composable
fun ToggleSetting(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = isChecked,
            onCheckedChange = {
                Log.d("SettingsScreen", "read aloud :$it")
                onCheckedChange(it)
            }
        )
    }
}

data class SpinnerItem<T>(val data: T, val displayName: String)

@Composable
fun <T> Spinner(
    items: List<SpinnerItem<T>>,
    selectedItem: SpinnerItem<T>,
    toolTip: String?,
    onSelection: (selection: T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    var showToolTip by remember { mutableStateOf(false) }
    Column {
        Box(
            Modifier
                .clickable(onClick = { expanded = true })
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = selectedItem.displayName.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )

            Icon(
                Icons.Default.Info,
                contentDescription = stringResource(R.string.info_icon_content_desc),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 40.dp)
                    .clickable(onClick = { showToolTip = !showToolTip }),
            )

            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.drop_down_arrow_content_desc),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        if (showToolTip) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.onBackground)

            ) {
                toolTip?.let {
                    Text(
                        text = toolTip,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                }

            }

        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)


        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onSelection(items[selectedIndex].data)
                }, text = {
                    Column() {
                        Text(
                            item.displayName.uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider()
                    }
                })
            }
        }

    }
}


@Composable
fun ErrorView(message: String) {
    var showError by remember { mutableStateOf(false) }
    showError = message.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


@Preview
@Composable
fun SettingScreenPreview() {
    SettingsScreen(
        uiState = SettingsUiState(),
        isExpanded = false,
        openDrawer = { },
        smartAnalytics = FakeAnalytics(),
        refreshErrorMessage = { },
        toggleReadAloud = {},
        toggleHandsFreeMode = {},
        chooseAiTool = {},
        chooseChatModel = {},
        onLogout = {}
    )
}
