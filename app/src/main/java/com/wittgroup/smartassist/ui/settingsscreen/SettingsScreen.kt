package com.wittgroup.smartassist.ui.settingsscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wittgroup.smartassist.ui.components.AppBar

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    Scaffold(topBar = {
        AppBar(title = "Settings")
    }, content = { padding ->
        Text(text = "Settings Screen", modifier = Modifier.padding(padding))
    })
}
