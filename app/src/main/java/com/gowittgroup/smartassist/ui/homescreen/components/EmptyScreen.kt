package com.gowittgroup.smartassist.ui.homescreen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.BannerAdView
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassist.util.Constants
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassist.util.isAndroidTV

@Composable
fun EmptyScreen(
    message: String,
    modifier: Modifier = Modifier,
    navigateToHistory: () -> Unit,
    navigateToPrompts: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToSummarize: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
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
            Text(text = stringResource(R.string.wondering_what_to_ask), style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable(onClick = {
                        navigateToPrompts()
                    })
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.check_sample_prompts_button_text),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Icon(
                    Icons.Outlined.ChevronRight,
                    stringResource(R.string.ic_chevron_right_desc),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            if(!Session.subscriptionStatus){
                Spacer(modifier = Modifier.height(32.dp))
                ExploreAiModelContent(navigateToSubscription)
            }
            if (!LocalContext.current.isAndroidTV()) {
                if(!Session.subscriptionStatus){
                    Spacer(modifier = Modifier.height(32.dp))
                    SummarizeNavContent(navigateToSubscription, navigateToSummarize)
                }
            }
        }
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
            navigateToPrompts = {},
            navigateToSubscription = {},
            navigateToSummarize = {}
        )
    }
}