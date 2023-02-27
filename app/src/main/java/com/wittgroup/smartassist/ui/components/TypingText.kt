package com.wittgroup.smartassist.ui.components

import android.animation.ValueAnimator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun TypingText(
    text: String,
    modifier: Modifier,
    style: TextStyle,
    durationPerChar: Int = 50
) {
    var animatedValue by remember { mutableStateOf(0f) }

    LaunchedEffect(text) {
        val charCount = text.length
        val duration = charCount * durationPerChar

        val valueAnimator = ValueAnimator.ofFloat(0f, charCount.toFloat())
        valueAnimator.duration = duration.toLong()
        valueAnimator.addUpdateListener {
            animatedValue = it.animatedValue as Float
        }
        valueAnimator.start()
    }

    Text(
        text = text.take(animatedValue.toInt()),
        style = style,
        modifier = modifier
    )
}
