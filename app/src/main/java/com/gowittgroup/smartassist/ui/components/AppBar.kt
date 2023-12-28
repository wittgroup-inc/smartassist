package com.gowittgroup.smartassist.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gowittgroup.smartassist.R

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
                        contentDescription = stringResource(R.string.menu_icon_content_desc),
                    )
                }
            }
        },
        actions = actions
    )
}

@Preview
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppBarPreview() {
    AppBar(
        title = "Preview",
        actions = {},
        openDrawer = {},
        isExpanded = false
    )
}

@Preview
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppBarExpandedPreview() {
    AppBar(
        title = "Preview",
        actions = {},
        openDrawer = {},
        isExpanded = true
    )
}