package com.gowittgroup.smartassist.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
fun EmptyScreen(message: String, modifier: Modifier = Modifier, navigateToHistory: () -> Unit, navigateToPrompts: () -> Unit) {

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(R.string.history_screen_title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = { navigateToHistory() })
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Wondering what to ask?", style = MaterialTheme.typography.headlineSmall)

            Row(modifier = Modifier
                .padding(top = 16.dp)
                .background(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = { navigateToPrompts() })
                .padding(start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = "Check Sample Prompts",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Icon(Icons.Outlined.ChevronRight, stringResource(R.string.ic_chevron_right_desc), modifier = Modifier.align(CenterVertically))
            }

        }
    }
}

@Composable
fun EmptyScreen(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}
