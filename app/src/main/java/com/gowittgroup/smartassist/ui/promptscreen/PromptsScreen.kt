package com.gowittgroup.smartassist.ui.promptscreen

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.EmptyScreen
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassistlib.models.Prompts

@Composable
fun PromptsScreen(
    uiState: PromptUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics,
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    resetErrorMessage: () -> Unit
) {

    logUserEntersEvent(smartAnalytics)

    ErrorView(uiState.error).also { resetErrorMessage() }

    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.prompts_screen_title),
            openDrawer = openDrawer,
            isExpanded = isExpanded
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            if (uiState.prompts.isEmpty()) {
                EmptyScreen(message = stringResource(R.string.empty_prompts_message), modifier = Modifier.padding(padding))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxHeight()
                        .padding(bottom = 16.dp)
                ) {

                    items(uiState.prompts) {
                        val isExpandMore = remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                                .background(color = Color.Transparent),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            HeaderItem(it, isExpandMore.value) { isExpandMore.value = !isExpandMore.value }
                            if (isExpandMore.value) {
                                Divider()
                            }
                            ContentItem(it, isExpandMore.value, smartAnalytics = smartAnalytics) { prompt ->
                                navigateToHome(
                                    null,
                                    it.category.descriptions + Prompts.JOINING_DELIMITER + prompt
                                )
                            }
                        }
                    }
                }

            }

        }

    })
}

@Composable
fun ContentItem(prompts: Prompts, isExpanded: Boolean, smartAnalytics: SmartAnalytics, onClick: (prompt: String) -> Unit) {
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

@Composable
fun HeaderItem(prompts: Prompts, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onClick() })
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = prompts.category.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = prompts.category.descriptions,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp, top = 4.dp)
            )
        }
        IconButton(onClick = { onClick() }, modifier = Modifier.size(24.dp)) {
            Icon(
                if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                if (isExpanded) stringResource(R.string.ic_drop_up_dec) else stringResource(R.string.ic_drop_down_desc)
            )
        }
    }
}


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "prompt_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}

private fun logUserClickedEvent(smartAnalytics: SmartAnalytics, itemName: String) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.ITEM_NAME, itemName)
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_CLICKED_ON, bundle)
}

@Composable
fun ErrorView(message: String) {
    var showError by remember { mutableStateOf(false) }
    showError = message.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


@Preview
@Composable
fun PromptsScreenPreview() {
    PromptsScreen(
        uiState =PromptUiState(),
        isExpanded = false,
        openDrawer = {  },
        smartAnalytics = FakeAnalytics(),
        navigateToHome = {_, _ ->},
        resetErrorMessage = {}
    )
}