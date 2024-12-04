package com.gowittgroup.smartassist.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
internal fun ProfileImage(url: String?) {
    AsyncImage(
        model = url
            ?: "https://api.dicebear.com/6.x/adventurer-neutral/png?seed=default",
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = 0.2f)),
        contentScale = ContentScale.Crop
    )
}