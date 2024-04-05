package com.gowittgroup.smartassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import kotlinx.coroutines.delay

@Composable
fun TripleDotProgressIndicator() {
    var dotCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 3
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .padding(horizontal = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(4.dp)
                        .background(
                            color = if (index == dotCount) {
                                Color.Black
                            } else {
                                Color.Gray
                            }, shape = CircleShape
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun TripleDotProgressIndicatorPreview() {
    SmartAssistTheme {
        TripleDotProgressIndicator()
    }
}