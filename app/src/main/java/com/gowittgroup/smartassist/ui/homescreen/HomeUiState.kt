package com.gowittgroup.smartassist.ui.homescreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.ui.homescreen.components.PromptMode
import com.gowittgroup.smartassistlib.domain.models.ClarifyingQuestion
import com.gowittgroup.smartassistlib.domain.models.Template
import com.gowittgroup.smartassistlib.models.banner.Banner
import java.util.UUID

data class HomeUiState(
    val textFieldValue: MutableState<TextFieldValue>,
    val conversations: List<Conversation>,
    val hint: String,
    val showLoading: Boolean,
    val micIcon: Boolean,
    val speechRecognizerState: SpeechRecognizerState = SpeechRecognizerState.Idle,
    val readAloud: MutableState<Boolean>,
    val error: MutableState<String>,
    val showHandsFreeAlertIsClosed: Boolean = false,
    val clarifyingQuestion: List<ClarifyingQuestion> = listOf(),
    val templates: List<Template> = listOf(),
    val selectedTemplate: Template? = null,
    val promptAssembly: String = "",
    val promptMode: PromptMode = PromptMode.NORMAL,
    val handsFreeMode: MutableState<Boolean> = mutableStateOf(false),
    val banner: Banner = Banner.EMPTY
): State {
    companion object {
        val DEFAULT = HomeUiState(
            textFieldValue = mutableStateOf(TextFieldValue("")),
            conversations = listOf(),
            hint = "Start typing...",
            showLoading = false,
            micIcon = false,
            readAloud = mutableStateOf(false),
            error = mutableStateOf("")
        )

        fun getId() = run { UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE }
    }
}