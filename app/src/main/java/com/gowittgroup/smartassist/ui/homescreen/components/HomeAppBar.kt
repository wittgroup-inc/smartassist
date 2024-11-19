package com.gowittgroup.smartassist.ui.homescreen.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    actions: @Composable RowScope.() -> Unit = {},
    openDrawer: () -> Unit = {},
    isExpanded: Boolean,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painterResource(id = R.drawable.ic_app_title),
                "SmartAssist",
                modifier = Modifier.height(32.dp),
                colorFilter =  ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
        },
        navigationIcon = {
            if(!isExpanded){
                IconButton(onClick = openDrawer) {
                    Icon(
                        Icons.Outlined.Menu,
                        contentDescription = stringResource(id = R.string.menu_icon_content_desc),
                    )
                }
            }
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview("ChatBar contents")
@Preview("ChatBar contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeAppBar() {
    SmartAssistTheme {
        HomeAppBar(isExpanded = false)
    }
}
