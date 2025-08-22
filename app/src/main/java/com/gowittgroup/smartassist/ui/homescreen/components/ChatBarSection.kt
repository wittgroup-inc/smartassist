package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.homescreen.HomeUiState

@Composable
internal fun ChatBarSection(
    uiState: HomeUiState,
    modifier: Modifier,
    onSend: () -> Unit,
    onActionUp: () -> Unit,
    onActionDown: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onTemplateSelected: (String) -> Unit,
    togglePromptMode: () -> Unit,
    onTemplateInputDone: (Map<String, String>) -> Unit,
    onWordTyped: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
    ) {
        ChatBar(
            state = uiState.textFieldValue,
            hint = uiState.hint,
            icon = if (uiState.micIcon) painterResource(R.drawable.ic_mic_on) else painterResource(
                R.drawable.ic_mic_off
            ),
            modifier = modifier.padding(16.dp),
            actionUp = onActionUp,
            actionDown = onActionDown,
            mode = uiState.promptMode,
            suggestions = uiState.clarifyingQuestion,
            onSuggestionClick = onSuggestionClick,
            onTemplateSelected = onTemplateSelected,
            templates = uiState.templates,
            togglePromptMode = togglePromptMode,
            selectedTemplate = uiState.selectedTemplate,
            onClick = { onSend() },
            onTemplateInputDone = onTemplateInputDone,
            onWordTyped = onWordTyped
        )
    }
}