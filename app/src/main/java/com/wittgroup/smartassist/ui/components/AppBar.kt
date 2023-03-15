package com.wittgroup.smartassist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    openDrawer: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
           Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "",
                )
            }
        },
        actions = actions
    )
}
