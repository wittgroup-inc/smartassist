package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gowittgroup.smartassist.R

@Composable
internal fun ComposeLottieAnimation(modifier: Modifier = Modifier) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.listening_animation))

    LottieAnimation(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        clipToCompositionBounds = true,
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}