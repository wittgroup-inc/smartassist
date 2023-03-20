package com.gowittgroup.smartassist.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    openDrawer: () -> Unit = {},
    isExpanded: Boolean
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if(!isExpanded){
                IconButton(onClick = openDrawer) {
                    Icon(
                        Icons.Outlined.Menu,
                        contentDescription = "",
                    )
                }
            }
        },
        actions = actions
    )
}
