package com.gowittgroup.smartassist.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SecondaryOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    fontSize: TextUnit = 16.sp,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 6.dp,
    buttonPadding: PaddingValues = PaddingValues(0.dp, 0.dp),
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(buttonPadding),
        enabled = enabled,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)  // Add primary color border
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.primary,  // Primary color for text
            modifier = Modifier.padding(horizontalPadding, verticalPadding)
        )
    }
}