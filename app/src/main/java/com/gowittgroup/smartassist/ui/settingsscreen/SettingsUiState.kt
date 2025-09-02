package com.gowittgroup.smartassist.ui.settingsscreen

import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassistlib.models.ai.AiModel
import com.gowittgroup.smartassistlib.models.ai.AiTools


data class SettingsUiState(
    val tools: List<AiTools> = emptyList(),
    val models: List<AiModel> = emptyList(),
    val readAloud: Boolean = false,
    val handsFreeMode: Boolean = false,
    val selectedAiModel: String = "",
    val selectedAiTool: AiTools = AiTools.CHAT_GPT,
    val loading: Boolean = false,
    val notificationState: NotificationState? = null
) : State