package com.gowittgroup.smartassist.ui.history.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory

@Composable
internal fun ConversationItem(
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    item: ConversationHistory,
    deleteHistory: (history: ConversationHistory) -> Unit,
    index: Int,
    conversations: List<ConversationHistory>
) {
    Column(modifier = Modifier.clickable {
        navigateToHome(
            item.conversationId,
            null
        )
    }) {
        val content = if (item.conversations.isNotEmpty()) {
            item.conversations.findLast { it.isQuestion }?.data
                ?: stringResource(R.string.new_chat)
        } else {
            stringResource(R.string.new_chat)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 14.dp,
                        bottom = 14.dp
                    )
            )

            IconButton(
                modifier = Modifier
                    .padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
                    .size(24.dp),
                onClick = { deleteHistory(item) },
                content = {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(R.string.delete_icon_content_desc),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                })
        }

    }
    if (index != conversations.lastIndex) {
        HorizontalDivider()
    }
}