package com.gowittgroup.smartassist.ui.settingsscreen

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.BuildConfig
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassistlib.models.AiTools

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics
) {
    val uiState by viewModel.uiState.collectAsState()

    logUserEntersEvent(smartAnalytics)

    ErrorView(uiState.error).also { viewModel.resetErrorMessage() }

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
                    uiState.readAloud
                ) {
                    viewModel.toggleReadAloud(it)
                }
                Divider()
                Spinner(
                    uiState.tools.filter { it != AiTools.NONE }
                        .map { SpinnerItem(it, it.displayName) },
                    SpinnerItem(uiState.selectedAiTool, uiState.selectedAiTool.displayName),
                    "Switch AI tools for better result."
                ) {
                    viewModel.chooseAiTool(it)
                }
                Divider()
                Spinner(
                    uiState.models.map { SpinnerItem(it, it) },
                    SpinnerItem(uiState.selectedAiModel, uiState.selectedAiModel),
                    "All models might not work. Select GPT-x.x..  should work perfectly."
                ) {
                    viewModel.chooseChatModel(it)
                }
                Divider()
                Text(
                    text = "UUID: ${uiState.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                Divider()
            }
        }

    },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "App Version: v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyMedium
                )
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
                        Divider()
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

