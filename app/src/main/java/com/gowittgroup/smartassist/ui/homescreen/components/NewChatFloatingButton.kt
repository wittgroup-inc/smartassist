package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NewChatFloatingButton(
    modifier: Modifier = Modifier,
    navigateToHome: (id: Long?, prompt: String?) -> Unit) {
    FloatingActionButton(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.surface,
        shape = CircleShape,
        content = {
            Icon(
                Icons.Default.Add, ""
            )
        },
        onClick = { navigateToHome(null, null) })
}