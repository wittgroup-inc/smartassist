package com.gowittgroup.smartassist.ui.history

import android.os.Bundle
import android.text.format.DateUtils
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.services.ads.AdService
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.EmptyScreen
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassist.ui.components.SearchBar
import com.gowittgroup.smartassist.ui.history.components.ConversationGroupHeader
import com.gowittgroup.smartassist.ui.history.components.ConversationItem
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    smartAnalytics: SmartAnalytics,
    deleteHistory: (history: ConversationHistory) -> Unit,
    onQueryChange: (q: String) -> Unit
) {
    logUserEntersEvent(smartAnalytics)
    val context = LocalContext.current
    var searchMode by remember {
        mutableStateOf(false)
    }

    val adService = remember { AdService() }
    adService.loadInterstitialAd(context)
    adService.showInterstitialAd(context)

    Scaffold(topBar = {
        if (searchMode) {
            CenterAlignedTopAppBar(
                title = {
                    SearchBar(
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        ),
                        value = uiState.query,
                        onQueryChange = onQueryChange,
                        onCloseSearch = {
                            onQueryChange("")
                            searchMode = false
                        }
                    )
                })

        } else {
            AppBar(
                title = stringResource(R.string.history_screen_title),
                openDrawer = openDrawer,
                isExpanded = isExpanded,
                actions = {
                    if (uiState.conversationHistory.isNotEmpty()) {
                        IconButton(onClick = { searchMode = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search)
                            )
                        }
                    }
                }
            )
        }

    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                if (uiState.conversationHistory.isEmpty()) {
                    item {
                        EmptyScreen(
                            modifier = Modifier.fillParentMaxSize(),
                            message = stringResource(
                                if (uiState.query.isNotEmpty())
                                    R.string.empty_conversation_search_result_msg
                                else
                                    R.string.conversation_history_empty_msg
                            ),
                        )
                    }
                } else {
                    val map = uiState.conversationHistory.groupBy(::getRelativeTimeString)
                    map.forEach { (date, conversations) ->
                        item {
                            ConversationGroupHeader(date)
                        }
                        itemsIndexed(conversations) { index, item ->
                            ConversationItem(
                                navigateToHome,
                                item,
                                deleteHistory,
                                index,
                                conversations
                            )
                        }
                    }
                    item {
                        HorizontalDivider()
                    }
                }
            }
        }
    })
}

private fun getRelativeTimeString(history: ConversationHistory): String {
    return DateUtils.getRelativeTimeSpanString(
        history.timestamp.time,
        System.currentTimeMillis(),
        DateUtils.DAY_IN_MILLIS
    ).toString()
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
        uiState = HistoryUiState(query = "Hello"),
        isExpanded = false,
        openDrawer = { },
        navigateToHome = { _, _ -> },
        smartAnalytics = FakeAnalytics(),
        deleteHistory = {},
        onQueryChange = {}
    )
}