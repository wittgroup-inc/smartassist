package com.gowittgroup.smartassist.ui.splashscreen

import android.content.res.Configuration
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navigateToHome: (id: Long?, prompt: String?) -> Unit) {
    val scale = remember {
        Animatable(0f)
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)
        navigateToHome(null, null)
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                val logo = painterResource(id = R.drawable.ic_bot_square)
                val logoAspectRatio = logo.intrinsicSize.width / logo.intrinsicSize.height
                Image(
                    painter = painterResource(id = R.drawable.ic_bot_square),
                    contentDescription = stringResource(R.string.logo_content_desc),
                    modifier = Modifier
                        .height(80.dp)
                        .aspectRatio(logoAspectRatio),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_app_title),
                    contentDescription = stringResource(R.string.title_logo_content_desc),
                    modifier = Modifier.padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Powered by ChatGPT",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.openai_log),
                    contentDescription = stringResource(R.string.chat_gpt_log_content_desc),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.scale(scale.value)
                )
            }
        }
    }
}


@Preview("Splash Screen")
@Preview("Splash Screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSplashScreen() {
    SmartAssistTheme {
        SplashScreen(
            navigateToHome = {_, _ ->},
        )
    }
}
