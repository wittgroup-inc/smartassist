package com.wittgroup.smartassist.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.components.AppBar
import com.wittgroup.smartassist.ui.components.LoadingScreen
import com.wittgroup.smartassistlib.db.entities.ConversationHistory


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

            LazyColumn {
                itemsIndexed(uiState.conversationHistory) { index, item ->
                    Column(modifier = Modifier.clickable { navigateToHome(item.conversationId) }) {
                        Text(text = item.conversationId.toString(), modifier = Modifier.padding(16.dp))
                        if (item.conversations.isNotEmpty()) {
                            val content =
                                if (item.conversations.last().data.length > 20) item.conversations.last().data.substring(20) else item.conversations.last().data
                            Text(text = content, modifier = Modifier.padding(16.dp))
                        }
                    }
                    Divider()
                }
            }
        }

    })
}

