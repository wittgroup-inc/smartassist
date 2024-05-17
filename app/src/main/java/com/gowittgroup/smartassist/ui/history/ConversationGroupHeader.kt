package com.gowittgroup.smartassist.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConversationGroupHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = .5f
                )
            )
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}