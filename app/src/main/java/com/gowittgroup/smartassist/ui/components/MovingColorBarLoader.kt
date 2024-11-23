package com.gowittgroup.smartassist.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@Composable
fun MovingColorBarLoader() {
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)

    val transition = rememberInfiniteTransition(label = "")

    val offsetX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val barWidth = size.width
        val colorWidth = barWidth / 3
        val animatedOffset = offsetX * barWidth

        val gradientBrush = Brush.horizontalGradient(
            colors = colors + colors.first(),
            startX = -animatedOffset,
            endX = barWidth - animatedOffset
        )

        drawRect(
            brush = gradientBrush,
            topLeft = Offset(0f, 0f),
            size = Size(barWidth, size.height),
            style = Fill
        )
    }
}

@Preview
@Composable
private fun MovingColorBarLoaderPrev() {
    SmartAssistTheme {
        MovingColorBarLoader()
    }
}