package com.gowittgroup.smartassist.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@Composable
fun LoadingScreen(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}


@Preview
@Composable
fun LoadingScreenPreview() {
    SmartAssistTheme {
        LoadingScreen(modifier = Modifier)
    }
}