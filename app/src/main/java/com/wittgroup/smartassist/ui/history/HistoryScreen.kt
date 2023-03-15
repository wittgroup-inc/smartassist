package com.wittgroup.smartassist.ui.history

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.components.AppBar
import com.wittgroup.smartassist.ui.components.LoadingScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel, isExpanded: Boolean,  openDrawer: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        AppBar(title = stringResource(R.string.history_screen_title),
            openDrawer = openDrawer
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn {
                items(uiState.conversationHistory) { item ->
                    Text(text = item.conversationId.toString(), modifier = Modifier.padding(16.dp))
                    Divider()
                }

            }
        }

    })
}
