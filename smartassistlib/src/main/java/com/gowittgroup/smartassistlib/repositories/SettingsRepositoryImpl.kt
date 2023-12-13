package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.models.Resource
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val aiDataSource: AiDataSource, private val settingsDataSource: SettingsDataSource) : SettingsRepository {

    override suspend fun getModels(): Resource<List<String>> = aiDataSource.getModels()

    override suspend fun getSelectedAiModel(): Resource<String> = settingsDataSource.getSelectedAiModel()

    override suspend fun getReadAloud(): Resource<Boolean> = settingsDataSource.getReadAloud()

    override suspend fun toggleReadAloud(isOn: Boolean) = settingsDataSource.toggleReadAloud(isOn)

    override suspend fun chooseAiModel(model: String) = settingsDataSource.chooseAiModel(model)

    override suspend fun getUserId(): Resource<String> = settingsDataSource.getUserId()
}
