package com.gowittgroup.smartassistlib.data.datasources.settings

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.AiTools


interface SettingsDataSource {
    suspend fun getSelectedAiModel(): Resource<String>
    suspend fun getSelectedAiTool(): Resource<AiTools>
    suspend fun getReadAloud(): Resource<Boolean>
    suspend fun toggleReadAloud(isOn: Boolean)
    suspend fun chooseAiModel(chatModel: String)
    suspend fun chooseAiTool(tool: AiTools)
    suspend fun getUserId(): Resource<String>
    suspend fun setUserId(userId: String?)
    suspend fun getDefaultChatModel(): String
    suspend fun toggleHandsFreeMode(isOn: Boolean)
    suspend fun getHandsFreeMode(): Resource<Boolean>
    suspend fun setUserSubscriptionStatus(active: Boolean)
    suspend fun getUserSubscriptionStatus(): Resource<Boolean>
}
