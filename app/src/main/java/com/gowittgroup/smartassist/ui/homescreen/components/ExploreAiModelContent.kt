package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
internal fun ExploreAiModelContent(
    navigateToSubscription: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        UpgradeText(content = stringResource(R.string.explore_more_models), navigateToSubscription)
    }
}