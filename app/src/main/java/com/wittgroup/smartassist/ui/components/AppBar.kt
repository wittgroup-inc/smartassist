package com.wittgroup.smartassist.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.wittgroup.smartassist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, actions: @Composable RowScope.() -> Unit = {},  openDrawer: () -> Unit = {} ) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(R.drawable.ic_bot),
                    contentDescription = "",
                )
            }
        },
        actions = actions
    )
}
