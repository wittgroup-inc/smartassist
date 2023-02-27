package com.wittgroup.smartassist.ui.components

import android.animation.ValueAnimator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.core.animation.doOnEnd

@Composable
fun TypingText(
    text: String,
    style: TextStyle,
    modifier: Modifier,
    durationPerChar: Int = 50,
    onComplete: () -> Unit = {}
) {
    var animatedValue by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            val charCount = text.length
            val duration = charCount * durationPerChar

            val valueAnimator = ValueAnimator.ofFloat(0f, charCount.toFloat())
            valueAnimator.duration = duration.toLong()
            valueAnimator.addUpdateListener {
                animatedValue = it.animatedValue as Float
            }
            valueAnimator.doOnEnd {
                onComplete()
                isAnimating = false
            }
            valueAnimator.start()
        }
    }

    Text(
        text = text.take(animatedValue.toInt()),
        style = style,
        modifier = modifier
    )
}
