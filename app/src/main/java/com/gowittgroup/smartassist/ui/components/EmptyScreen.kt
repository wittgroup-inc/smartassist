package com.gowittgroup.smartassist.ui.components


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.services.ads.AdService
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassist.util.Constants

@Composable
fun EmptyScreen(
    message: String,
    modifier: Modifier = Modifier,
    navigateToHistory: () -> Unit,
    navigateToPrompts: () -> Unit
) {
    val context = LocalContext.current
    val adState = remember { AdService() }
    adState.loadInterstitialAd(context)
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BannerAdView(adUnitId = Constants.HOME_TOP_BANNER_AD_UNIT_ID)
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(R.string.history_screen_title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = { navigateToHistory() })
            )

            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Wondering what to ask?", style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable(onClick = {
                        adState.showInterstitialAd(context = context)
                        navigateToPrompts()
                    })
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = "Check Sample Prompts",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Icon(
                    Icons.Outlined.ChevronRight,
                    stringResource(R.string.ic_chevron_right_desc),
                    modifier = Modifier.align(CenterVertically)
                )
            }
        }
    }
}

@Composable
fun EmptyScreen(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}


@Preview
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmptyScreenPreview() {
    SmartAssistTheme {
        EmptyScreen(message = "There is no data to show.")
    }
}

@Preview
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmptyScreenNavigationPreview() {
    SmartAssistTheme {
        EmptyScreen(
            message = "There is no data to show.",
            navigateToHistory = {},
            navigateToPrompts = {})
    }
}