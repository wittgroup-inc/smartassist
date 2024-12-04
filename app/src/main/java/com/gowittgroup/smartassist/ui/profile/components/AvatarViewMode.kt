package com.gowittgroup.smartassist.ui.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AvatarViewMode(
    url: String?
) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .size(120.dp)
            .padding(16.dp)
    ) {
        ProfileImage(url = url)
    }
}