package com.gowittgroup.smartassist.ui.summary.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassist.util.copyTextToClipboard
import com.gowittgroup.smartassist.util.share

@Composable
internal fun SummaryFooter(
    aiTool: String,
    item: String,
    context: Context = LocalContext.current
) {
    Column {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modifier = Modifier
                .size(32.dp)
                .border(
                    BorderStroke(1.dp, color = Color.Black),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)

            Text(stringResource(R.string.summary_powered_by, aiTool), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    copyTextToClipboard(
                        context,
                        item
                    )
                }
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    modifier = modifier,
                    contentDescription = stringResource(R.string.copy)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = {
                    context.share(
                        item, context.getString(R.string.summary),
                        context.getString(R.string.choose)
                    )
                }
            ) {
                Icon(
                    Icons.Default.Share,
                    modifier = modifier,
                    contentDescription = stringResource(R.string.share)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SummaryFooterPreview() {
    SmartAssistTheme {
        SummaryFooter(aiTool = "ChatGPT", item = "Copy Me")
    }
}