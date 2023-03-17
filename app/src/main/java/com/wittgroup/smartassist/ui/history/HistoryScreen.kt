package com.wittgroup.smartassist.ui.history

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.components.AppBar
import com.wittgroup.smartassist.ui.components.LoadingScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel, isExpanded: Boolean, openDrawer: () -> Unit, navigateToHome: (id: Long?) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.history_screen_title),
            openDrawer = openDrawer
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(uiState.conversationHistory) { item ->
                    Column(modifier = Modifier.clickable { navigateToHome(item.conversationId) }) {
                        val content = if (item.conversations.isNotEmpty()) {
                            item.conversations.findLast { it.isQuestion }?.data ?: stringResource(R.string.new_chat)
                        } else {
                            stringResource(R.string.new_chat)
                        }

                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                                .fillMaxWidth()
                        )

                        Text(
                            text = DateUtils.getRelativeTimeSpanString(item.timestamp.time).toString(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth()
                        )
                    }
                    Divider()
                }
            }
        }

    })
}

