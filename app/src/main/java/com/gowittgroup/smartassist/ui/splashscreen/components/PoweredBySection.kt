package com.gowittgroup.smartassist.ui.splashscreen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
internal fun PoweredBySection(
    modifier: Modifier = Modifier,
    scale: Animatable<Float, AnimationVector1D>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 16.dp, bottom = 32.dp)
    )
    {
        Text(
            text = "Powered by",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier.padding(
                bottom = 8.dp
            )
        )

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {

            ServiceProvider(
                modifier = modifier,
                scale = scale,
                providerName = R.string.service_provider_openai,
                providerIcon = R.drawable.openai_logo
            )
            HorizontalDivider(
                modifier
                    .width(1.dp)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
            )
            ServiceProvider(
                modifier = modifier,
                scale = scale,
                providerName = R.string.provider_name_google,
                providerIcon = R.drawable.gemini_logo
            )
        }

    }
}