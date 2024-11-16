package com.gowittgroup.smartassist.ui.splashscreen

import android.content.res.Configuration
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassist.util.Session
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
        delay(1000L)
        ready = true

    }

    if (ready) {
        Navigate(
            navigateToHome = navigateToHome,
            navigateToSignIn = navigateToSignIn
        )
    }

    // Image
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

@Composable
private fun AppLogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
}

@Composable
private fun PoweredBySection(
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

@Composable
private fun ServiceProvider(
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

@Composable
private fun Navigate(
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    navigateToSignIn: () -> Unit = {}
) {
    val currentUser = Session.currentUser.collectAsState()
    Log.d("Pawan >> Splash", "Navigate ${currentUser.value}")
    if (currentUser.value != null) {
        navigateToHome(null, null)
    } else {
        navigateToSignIn()
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
