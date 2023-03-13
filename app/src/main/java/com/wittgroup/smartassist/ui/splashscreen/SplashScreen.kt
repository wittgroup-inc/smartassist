package com.wittgroup.smartassist.ui.splashscreen

import android.content.res.Configuration
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.AppNavRail
import com.wittgroup.smartassist.ui.navigation.SmartAssistDestinations
import com.wittgroup.smartassist.ui.theme.Purple40
import com.wittgroup.smartassist.ui.theme.Purple80
import com.wittgroup.smartassist.ui.theme.SmartAssistTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navigateToHome: () -> Unit) {
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
        navigateToHome()
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                val logo = painterResource(id = R.drawable.ic_bot_square)
                val logoAspectRatio = logo.intrinsicSize.width / logo.intrinsicSize.height
                Image(
                    painter = painterResource(id = R.drawable.ic_bot_square),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(80.dp)
                        .aspectRatio(logoAspectRatio),
                    colorFilter = ColorFilter.tint(Purple40)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_app_title),
                    contentDescription = "SmartAssist",
                    modifier = Modifier.padding(8.dp),
                            colorFilter = ColorFilter.tint(Purple40)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Powered by ChatGPT",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.openai_log),
                    contentDescription = "ChatGPT",
                    modifier = Modifier.scale(scale.value)
                )
            }
        }
    }
}


@Preview("Splash Screen")
@Preview("Splash Screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRail() {
    SmartAssistTheme {
        SplashScreen(
            navigateToHome = {},
        )
    }
}
