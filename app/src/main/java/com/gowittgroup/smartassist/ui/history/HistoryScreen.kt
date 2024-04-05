package com.gowittgroup.smartassist.ui.history

import android.os.Bundle
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.EmptyScreen
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory


@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    smartAnalytics: SmartAnalytics,
    deleteHistory: (history: ConversationHistory) -> Unit
) {
    logUserEntersEvent(smartAnalytics)

    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.history_screen_title),
            openDrawer = openDrawer,
            isExpanded = isExpanded
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            if (uiState.conversationHistory.isEmpty()) {
                EmptyScreen(
                    stringResource(R.string.conversation_history_empty_msg),
                    modifier = Modifier.padding(padding)
                )
            } else {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(uiState.conversationHistory) { item ->
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
                                        .padding(start = 16.dp, end = 8.dp, top = 8.dp)
                                )

                                IconButton(
                                    modifier = Modifier
                                        .padding(end = 16.dp, top = 8.dp)
                                        .size(24.dp),
                                    onClick = { deleteHistory(item) },
                                    content = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.delete_icon_content_desc),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    })
                            }

                            Text(
                                text = DateUtils.getRelativeTimeSpanString(item.timestamp.time)
                                    .toString(),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 4.dp)
                                    .fillMaxWidth()
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

    })
}

private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "history_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}


@Preview
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(
        uiState = HistoryUiState(),
        isExpanded = false,
        openDrawer = { },
        navigateToHome = {_, _ ->  },
        smartAnalytics = FakeAnalytics(),
        deleteHistory = {}
    )
}