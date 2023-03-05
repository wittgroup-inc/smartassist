package com.wittgroup.smartassist.ui

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wittgroup.smartassist.R
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wittgroup.smartassist.ui.theme.Purple500
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navHostController: NavHostController) {
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
        navHostController.navigate(SmartAssistDestinations.HOME_ROUTE)
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.smartassist_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.scale(scale.value)

                )
                Text(
                    text = "SMART ASSIST",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Purple500),
                    modifier = Modifier.padding(8.dp)
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
