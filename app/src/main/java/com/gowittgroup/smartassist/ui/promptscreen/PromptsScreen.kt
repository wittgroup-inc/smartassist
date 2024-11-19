package com.gowittgroup.smartassist.ui.promptscreen

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassist.ui.homescreen.components.EmptyScreen
import com.gowittgroup.smartassist.ui.promptscreen.components.ContentItem
import com.gowittgroup.smartassist.ui.promptscreen.components.ErrorView
import com.gowittgroup.smartassist.ui.promptscreen.components.HeaderItem
import com.gowittgroup.smartassistlib.models.prompts.Prompts

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


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "prompt_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}

fun logUserClickedEvent(smartAnalytics: SmartAnalytics, itemName: String) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.ITEM_NAME, itemName)
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_CLICKED_ON, bundle)
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