package com.gowittgroup.smartassist.ui.homescreen.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun ErrorView(message: MutableState<String>) {
    var showError by remember { mutableStateOf(false) }
    showError = message.value.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message.value, Toast.LENGTH_SHORT).show()
    }
}