package com.gowittgroup.smartassist.ui.splashscreen

import android.content.res.Configuration
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.gowittgroup.smartassist.ui.splashscreen.components.AppLogoSection
import com.gowittgroup.smartassist.ui.splashscreen.components.Navigate
import com.gowittgroup.smartassist.ui.splashscreen.components.PoweredBySection
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    navigateToSignUp: () -> Unit = {},
    navigateToSignIn: () -> Unit = {}
) {
    val scale = remember {
        Animatable(0f)
    }

    var ready by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(1000L)
        ready = true

    }

    if (ready) {
        Navigate(
            navigateToHome = navigateToHome,
            navigateToSignIn = navigateToSignIn
        )
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .semantics { contentDescription = "Splash Screen" }

    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(1f))
            AppLogoSection()
            Spacer(modifier = Modifier.weight(1f))
            PoweredBySection(scale = scale)
        }
    }
}


@Preview("Splash Screen")
@Preview("Splash Screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSplashScreen() {
    SmartAssistTheme {
        SplashScreen(
            navigateToHome = { _, _ -> },
        )
    }
}
