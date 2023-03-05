package com.wittgroup.smartassist.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun AppBar(title: String, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(text = title) },
        actions = actions
    )
}
