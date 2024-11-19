package com.gowittgroup.smartassist.ui.promptscreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.promptscreen.logUserClickedEvent
import com.gowittgroup.smartassistlib.models.prompts.Prompts

@Composable
internal fun ContentItem(prompts: Prompts, isExpanded: Boolean, smartAnalytics: SmartAnalytics, onClick: (prompt: String) -> Unit) {
    if (isExpanded) {
        Column {
            prompts.prompts.forEachIndexed { index, prompt ->

                Row(
                    modifier = Modifier
                        .clickable(onClick = {
                            onClick(prompt)
                            logUserClickedEvent(smartAnalytics, prompts.category.title)
                        })
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    Icon(
                        Icons.Outlined.ChevronRight,
                        stringResource(R.string.ic_chevron_right_desc),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(24.dp)
                    )
                }

                if (index != prompts.prompts.lastIndex) {
                    Divider()
                }
            }
        }
    }
}