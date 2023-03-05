package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.datasources.AiDataSource
import com.wittgroup.smartassistlib.datasources.SettingsDataSource
import com.wittgroup.smartassistlib.models.Resource

class SettingsRepositoryImpl(private val aiDataSource: AiDataSource, private val settingsDataSource: SettingsDataSource) : SettingsRepository {

    override suspend fun getModels(): Resource<List<String>> = aiDataSource.getModels()
    override suspend fun getSelectedAiModel(): Resource<String> = settingsDataSource.getSelectedAiModel()
    override suspend fun getReadAloud(): Resource<Boolean> = settingsDataSource.getReadAloud()

    override suspend fun toggleReadAloud(isOn: Boolean) = settingsDataSource.toggleReadAloud(isOn)

    override suspend fun chooseAiModel(model: String) = settingsDataSource.chooseAiModel(model)
}
