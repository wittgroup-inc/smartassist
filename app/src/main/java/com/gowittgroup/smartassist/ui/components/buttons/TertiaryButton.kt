package com.gowittgroup.smartassist.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TertiaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    fontSize: TextUnit = 16.sp,
    textPadding: PaddingValues = PaddingValues(0.dp, 0.dp),
    enabled: Boolean = true
) {
    TextButton(onClick = onClick, modifier = modifier.padding(textPadding), enabled = enabled) {
        Text(
            text = text,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.primary  // Primary color for text
        )
    }
}