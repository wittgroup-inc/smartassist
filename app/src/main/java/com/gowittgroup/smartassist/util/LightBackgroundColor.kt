package com.gowittgroup.smartassist.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun lightBackgroundColor(): Color = MaterialTheme.colorScheme.surfaceVariant.copy(.5f)