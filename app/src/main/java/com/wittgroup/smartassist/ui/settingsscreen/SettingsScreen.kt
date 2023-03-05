package com.wittgroup.smartassist.ui.settingsscreen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.ui.components.AppBar
import com.wittgroup.smartassist.ui.components.LoadingScreen

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = {
        AppBar(title = "Settings")
    }, content = { padding ->
        padding
        if (uiState.loading) {
            LoadingScreen()
        } else {
            Column() {
                ToggleSetting(title = "Read Aloud", uiState.readAloud) {
                    viewModel.toggleReadAloud(it)
                }
                Divider()
                Spinner(uiState.models, uiState.selectedAiModel) {
                    viewModel.chooseChatModel(it)
                }
                Divider()
            }
        }

    })
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
        Text(text = title, style = MaterialTheme.typography.body1)
        Switch(
            checked = isChecked,
            onCheckedChange = {
                Log.d("SettingsScreen", "read aloud :$it")
                onCheckedChange(it)
            }
        )
    }
}

@Composable
fun Spinner(items: List<String>, selectedItem: String, onSelection: (selection: String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    if (items.isEmpty()) return
    Column() {
        Box(
            Modifier
                .clickable(onClick = { expanded = true })
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = selectedItem.uppercase(),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .height(240.dp)

        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onSelection(items[selectedIndex])
                }) {
                    Column() {
                        Text(item.uppercase(), style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
                        Divider()
                    }

                }
            }
        }

    }
}


