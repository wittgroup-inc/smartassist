package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
internal fun UpgradeText(navigateToSubscription: () -> Unit) {
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.summarize_feature_desc))
            append(" ")
            pushStringAnnotation(tag = "URL", annotation = "")
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(R.string.upgrade_now))
            }
            append(stringResource(R.string.rocket_emoji))
            pop()
        },
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                navigateToSubscription()
            }
    )
}