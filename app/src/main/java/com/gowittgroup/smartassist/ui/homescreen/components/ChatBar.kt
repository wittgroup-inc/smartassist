package com.gowittgroup.smartassist.ui.homescreen.components

import android.content.res.Configuration
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassistlib.domain.models.ClarifyingQuestion
import com.gowittgroup.smartassistlib.domain.models.Template

enum class PromptMode { NORMAL, ASSIST, TEMPLATE }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ChatBar(
    state: MutableState<TextFieldValue>,
    hint: String,
    modifier: Modifier = Modifier,
    icon: Painter,
    actionUp: () -> Unit,
    actionDown: () -> Unit,
    onClick: () -> Unit,
    mode: PromptMode,
    suggestions: List<ClarifyingQuestion>,
    onSuggestionClick: (String) -> Unit,
    templates: List<Template>,
    onTemplateSelected: (String) -> Unit,
    togglePromptMode: () -> Unit,
    selectedTemplate: Template? = null,
    onTemplateInputDone: (Map<String, String>) -> Unit,
    onWordTyped: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Show smart inline suggestions depending on mode
        when (mode) {
            PromptMode.ASSIST -> {
                if (suggestions.isNotEmpty()) {
                    AssistInlineHints(
                        hints = suggestions.map { it.question },
                        onHintClick = { selectedHint ->
                            state.value = TextFieldValue(selectedHint)
                        }
                    )
                }
            }
            PromptMode.TEMPLATE -> {
                TemplateSelector(
                    selected = selectedTemplate,
                    templates = templates,
                    onTemplateSelected = onTemplateSelected
                )
            }
            PromptMode.NORMAL -> {
                if (suggestions.isNotEmpty()) {
                    SuggestionRow(suggestions, onSuggestionClick)
                }
            }
        }

        // Input Bar
        Box(
            modifier = Modifier
                .heightIn(min = TextFieldDefaults.MinHeight)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = togglePromptMode) {
                    val modeIcon = when (mode) {
                        PromptMode.NORMAL -> R.drawable.ic_chat
                        PromptMode.ASSIST -> R.drawable.ic_assist
                        PromptMode.TEMPLATE -> R.drawable.ic_template
                    }
                    Icon(
                        painter = painterResource(id = modeIcon),
                        contentDescription = "Mode toggle",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Input field

                if(mode == PromptMode.TEMPLATE && selectedTemplate != null){
                    val inputFinished = remember { mutableStateOf(false) }
                    if(!inputFinished.value){
                        TemplateStepper(
                            template = selectedTemplate,
                            onComplete = {
                                onTemplateInputDone(it)
                                inputFinished.value = true
                            }
                        )
                    } else {
                        TextField(
                            value = state.value,
                            onValueChange = { state.value = it },
                            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                            colors = TextFieldDefaults.colors().copy(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            placeholder = { Text(text = hint) },
                            modifier = Modifier.weight(1f),
                            maxLines = 4,
                        )
                    }

                } else {
                    TextField(
                        value = state.value,
                        onValueChange = { newValue ->
                            val oldValue = state.value
                            state.value = newValue
                            if (mode == PromptMode.ASSIST) {
                                val lastChar = newValue.text.takeLast(1)
                                if (lastChar == " ") {
                                    // User finished a word -> call ViewModel
                                    onWordTyped(newValue.text.trim())
                                }
                            }

                                        },
                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                        colors = TextFieldDefaults.colors().copy(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        placeholder = { Text(text = hint) },
                        modifier = Modifier.weight(1f),
                        maxLines = 4,
                    )
                }


                // Mic / Send logic (unchanged)
                Box(contentAlignment = Alignment.Center) {
                    if (state.value.text.isEmpty()) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.pointerInteropFilter { event ->
                                when (event.action) {
                                    MotionEvent.ACTION_DOWN -> { actionDown(); true }
                                    MotionEvent.ACTION_UP -> { actionUp(); true }
                                    else -> false
                                }
                            }
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = stringResource(R.string.mic_icon_content_desc),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        IconButton(onClick = { onClick() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                contentDescription = stringResource(R.string.send_icon_desc),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AssistInlineHints(
    hints: List<String>,
    onHintClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (hints.isEmpty()) return

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(hints) { hint ->
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .clickable { onHintClick(hint) }
            ) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun TemplateStepper(
    template: Template,
    onComplete: (Map<String, String>) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var answers by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var input by remember { mutableStateOf(TextFieldValue()) }

    if (currentStep < template.placeholders.size) {
        val currentField = template.placeholders[currentStep]

        Column(Modifier.padding(8.dp)) {
            Text("Please provide: $currentField", style = MaterialTheme.typography.bodyMedium)

            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Enter $currentField") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    answers[currentField] = input.text
                    input = TextFieldValue("")
                    if (currentStep == template.placeholders.lastIndex) {
                        onComplete(answers)
                    } else {
                        currentStep++
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(if (currentStep == template.placeholders.lastIndex) "Finish" else "Next")
            }
        }
    }
}

@Composable
private fun SuggestionRow(suggestions: List<ClarifyingQuestion>, onClick: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        suggestions.forEach {
            AssistChip(
                onClick = { onClick(it.id) },
                label = { Text(it.question, style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}

@Composable
private fun TemplateSelector(
    selected: Template?,
    templates: List<Template>,
    onTemplateSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth().padding(4.dp)) {
        Text(
            text = selected?.title ?: "Pick a template",
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            templates.forEach {
                DropdownMenuItem(
                    text = { Text(it.title) },
                    onClick = {
                        onTemplateSelected(it.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview("ChatBar contents")
@Preview("ChatBar contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatBar() {
    SmartAssistTheme {
        ChatBar(
            state = remember {
                mutableStateOf(TextFieldValue())
            },
            hint = "Enter text here",
            modifier = Modifier,
            icon = painterResource(id = R.drawable.ic_mic_on),
            mode = PromptMode.NORMAL,
            suggestions =  listOf(ClarifyingQuestion("1", "Question 1"), ClarifyingQuestion("2", "Question 2")),
            selectedTemplate = Template("1", "Template 1", "Description 1", listOf("Pace holder 1")),
            templates = listOf(Template("1", "Template 1", "Description 1", listOf("Pace holder 1")), Template("2", "Template 2", "Description 2", listOf("Pace holder 2"))),
            onSuggestionClick = {},
            onTemplateSelected = {},
            actionUp = {},
            actionDown = { },
            onClick = {},
            togglePromptMode = {},
            onTemplateInputDone = {},
            onWordTyped = {}
        )
    }
}
