package com.gowittgroup.smartassist.ui.homescreen.components

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.util.copyTextToClipboard

@Composable
internal fun ConversationSection(
    conversations: List<Conversation>,
    modifier: Modifier,
    navigateToHistory: () -> Unit,
    navigateToPrompts: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToSummarize: () -> Unit,
    listState: LazyListState,
    context: Context
) {
    if (conversations.isEmpty()) {
        EmptyScreen(
            stringResource(R.string.empty_chat_screen_message),
            modifier,
            navigateToHistory = navigateToHistory,
            navigateToPrompts = navigateToPrompts,
            navigateToSubscription = navigateToSubscription,
            navigateToSummarize = navigateToSummarize
        )
    } else {
        ConversationView(
            modifier = modifier,
            list = conversations,
            listState = listState,
            onCopy = { text -> copyTextToClipboard(context, text) }
        )
    }
}