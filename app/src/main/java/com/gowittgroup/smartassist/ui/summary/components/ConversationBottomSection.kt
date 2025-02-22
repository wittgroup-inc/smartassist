package com.gowittgroup.smartassist.ui.summary.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.util.copyTextToClipboard
import com.gowittgroup.smartassist.util.share

@Composable
fun ConversationBottomSection(
    item: String,
    context: Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        val modifier = Modifier
            .size(32.dp)
            .border(
                BorderStroke(1.dp, color = Color.Black),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)

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
                    item, "Summary",
                    "Choose"
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