package com.wittgroup.smartassistlib.datasources

import com.wittgroup.smartassistlib.models.Resource

interface SettingsDataSource {
    suspend fun getSelectedAiModel(): Resource<String>
    suspend fun getReadAloud(): Resource<Boolean>

    suspend fun toggleReadAloud(isOn: Boolean)
    suspend fun chooseAiModel(chatModel: String)
}
