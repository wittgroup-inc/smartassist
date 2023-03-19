package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.models.Resource

interface SettingsRepository {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getSelectedAiModel(): Resource<String>
    suspend fun getReadAloud(): Resource<Boolean>

    suspend fun toggleReadAloud(isOn: Boolean)
    suspend fun chooseAiModel(chatModel: String)
}
