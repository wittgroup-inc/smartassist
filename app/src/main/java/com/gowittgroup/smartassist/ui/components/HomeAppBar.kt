package com.gowittgroup.smartassist.ui.components

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
import com.gowittgroup.smartassist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    actions: @Composable RowScope.() -> Unit = {},
    openDrawer: () -> Unit = {},
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painterResource(id = R.drawable.ic_app_title),
                "",
                modifier = Modifier.height(32.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "",
                )
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
    )
}
