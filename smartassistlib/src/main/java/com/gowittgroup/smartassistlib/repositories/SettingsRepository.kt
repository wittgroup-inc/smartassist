package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Resource

interface SettingsRepository {

    suspend fun getAiTools(): Resource<List<AiTools>>
    suspend fun getModels(): Resource<List<String>>
    suspend fun getSelectedAiModel(): Resource<String>
    suspend fun getSelectedAiTool(): Resource<AiTools>
    suspend fun getReadAloud(): Resource<Boolean>
    suspend fun toggleReadAloud(isOn: Boolean)
    suspend fun chooseAiModel(chatModel: String)
    suspend fun chooseAiTool(tool: AiTools)
    suspend fun getUserId(): Resource<String>
    suspend fun getDefaultChatModel(): String
}
