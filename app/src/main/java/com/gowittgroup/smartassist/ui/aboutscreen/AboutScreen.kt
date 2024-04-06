package com.gowittgroup.smartassist.ui.aboutscreen

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.BuildConfig
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassist.util.openLink
import com.gowittgroup.smartassist.util.share


@Composable
fun AboutScreen(
    uiState: AboutUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics,
    navigateToFaq: () -> Unit,
    refreshErrorMessage: () -> Unit
) {

    logUserEntersEvent(smartAnalytics)
    val context = LocalContext.current
    ErrorView(uiState.error).also { refreshErrorMessage() }

    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.about_screen_title),
            openDrawer = openDrawer,
            isExpanded = isExpanded
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            Column(modifier = Modifier.padding(padding)) {

                Text(
                    text = "FAQ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navigateToFaq() }
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                HorizontalDivider()

                Text(
                    text = "Share",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { context.share("Download this amazing app SmartAssist - An Ai enabled ChatBot \n https://play.google.com/store/apps/details?id=com.gowittgroup.smartassist&hl=en_IN&gl=US", "SmartAssist:An AI enabled ChatBot", "Share With") }
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                HorizontalDivider()

                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            context.openLink("https://www.termsfeed.com/live/b6fd3ae8-506a-4b4f-a971-7230c0df4b46")
                        }
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                HorizontalDivider()

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                       context.openLink("https://www.buymeacoffee.com/pawankgupta_se")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    border = BorderStroke(1.dp, Color(0xFF65451F))

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Coffee,
                            contentDescription = "Buy me a coffee!",
                            tint = Color(0xFF65451F),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buy me a coffee",
                            style = TextStyle(
                                fontWeight = FontWeight.W600,
                                color = Color(0xFF65451F)
                            )
                        )
                    }

                }
            }
        }

    },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "App Version: v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}




private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "settings_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}




@Composable
fun ErrorView(message: String) {
    var showError by remember { mutableStateOf(false) }
    showError = message.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen(
        uiState = AboutUiState(),
        isExpanded = false,
        openDrawer = { },
        smartAnalytics = FakeAnalytics(),
        refreshErrorMessage = { },
        navigateToFaq = {}
    )
}
