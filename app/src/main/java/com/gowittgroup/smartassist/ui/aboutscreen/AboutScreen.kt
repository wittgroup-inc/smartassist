package com.gowittgroup.smartassist.ui.aboutscreen

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.BuildConfig
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.aboutscreen.components.ErrorView
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
            LazyColumn(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.faq_screen_title),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                logOptionClickedEvent(smartAnalytics, "FAQ")
                                navigateToFaq()
                            }
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    )
                    HorizontalDivider()

                    Text(
                        text = stringResource(id = R.string.share),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                logOptionClickedEvent(smartAnalytics, "Share")
                                context.share(
                                    "Download this amazing app SmartAssist - An AI enabled ChatBot \n https://play.google.com/store/apps/details?id=com.gowittgroup.smartassist&hl=en_IN&gl=US",
                                    "SmartAssist:An AI enabled ChatBot",
                                    "Share With"
                                )
                            }
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    )
                    HorizontalDivider()
                    Text(
                        text = stringResource(R.string.rate_app),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                logOptionClickedEvent(smartAnalytics, "Rate App")
                                appReview(context)
                            }
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    )
                    HorizontalDivider()

                    Text(
                        text = stringResource(R.string.privacy_policy),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                logOptionClickedEvent(smartAnalytics, "Privacy Policy")
                                context.openLink("https://www.termsfeed.com/live/b6fd3ae8-506a-4b4f-a971-7230c0df4b46")
                            }
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    )
                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedButton(
                        onClick = {
                            logUserClickedBuyMeACoffee(smartAnalytics, "Buy me a Coffee")
                            context.openLink("https://www.buymeacoffee.com/pawankgupta_se")
                        },

                        border = BorderStroke(1.dp, Color(0xFF65451F))

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Coffee,
                                contentDescription = stringResource(R.string.buy_me_a_coffee),
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
                    Spacer(modifier = Modifier.height(24.dp))
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
                    text = stringResource(R.string.app_version_v, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

private fun appReview(context: Context) {
    val manager = ReviewManagerFactory.create(context)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {

            val reviewInfo = task.result
            val flow = manager.launchReviewFlow(context as Activity, reviewInfo)

            flow.addOnCompleteListener { data ->
                if (data.isSuccessful) {
                    SmartLog.d("About Screen", "Review finished")
                } else {
                    Toast.makeText(context,
                        context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                    SmartLog.e("AboutScreen", "Error in rating app.")
                }



            }
        } else {

            Toast.makeText(context,
                context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            if (task.exception !is ReviewException) return@addOnCompleteListener
            @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
            SmartLog.e("AboutScreen", "Error in rating app: $reviewErrorCode")
        }
    }
}


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "about_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}

private fun logOptionClickedEvent(smartAnalytics: SmartAnalytics, option: String) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.ITEM_NAME, option)
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_CLICKED_ON, bundle)
}

private fun logUserClickedBuyMeACoffee(smartAnalytics: SmartAnalytics, option: String) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.ITEM_NAME, option)
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_TRIED_DONATING, bundle)
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
