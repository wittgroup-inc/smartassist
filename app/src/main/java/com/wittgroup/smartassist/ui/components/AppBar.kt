package com.wittgroup.smartassist.ui.components

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBar(title: String) {
    TopAppBar(
        title = { Text(text = title) }
    )
}
