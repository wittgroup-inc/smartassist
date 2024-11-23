package com.gowittgroup.smartassist.ui.splashscreen.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
internal fun ServiceProvider(
    modifier: Modifier,
    scale: Animatable<Float, AnimationVector1D>,
    @StringRes providerName: Int,
    @DrawableRes providerIcon: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(88.dp)
    ) {
        Text(
            text = stringResource(id = providerName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier.padding(
                bottom = 4.dp
            )
        )
        Image(
            painter = painterResource(id = providerIcon),
            contentDescription = stringResource(R.string.provider_name_google),
            modifier = modifier
                .height(32.dp)
                .scale(scale.value)
        )
    }
}