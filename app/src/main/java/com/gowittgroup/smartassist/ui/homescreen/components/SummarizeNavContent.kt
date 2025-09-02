package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
internal fun SummarizeNavContent(
    navigateToSubscription: () -> Unit,
    navigateToSummarize: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.try_summarize),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        UpgradeText(content = stringResource(R.string.summarize_feature_desc), navigateToSubscription)
    }

    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .background(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = {
                navigateToSummarize()
            })
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.summary_screen_title),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}