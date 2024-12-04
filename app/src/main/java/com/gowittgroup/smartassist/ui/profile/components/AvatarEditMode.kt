package com.gowittgroup.smartassist.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun AvatarEditMode(
    url: String?,
    showPicker: () -> Unit
) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .size(120.dp)
            .padding(16.dp)
            .clickable { showPicker() }
    ) {
        ProfileImage(url = url)
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Icon",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)

                .background(
                    Color.DarkGray.copy(alpha = 0.7f),
                    shape = CircleShape
                )
                .padding(4.dp)
                .align(Alignment.TopEnd)
        )
    }
}