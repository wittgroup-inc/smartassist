package com.gowittgroup.smartassist.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

            Spacer(modifier = Modifier.height(100.dp))

            Text(text = "Wondering what to ask?", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Check Sample Prompts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = { navigateToPrompts() })
            )
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
