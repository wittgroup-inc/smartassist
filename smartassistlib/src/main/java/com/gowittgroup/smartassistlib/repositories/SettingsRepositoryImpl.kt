package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.AiDataSourceProvider
import com.gowittgroup.smartassistlib.datasources.AiToolsDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Resource
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataSourceProvider: AiDataSourceProvider,
    private val settingsDataSource: SettingsDataSource,
    private val aiToolsDataSource: AiToolsDataSource
) : SettingsRepository {
    override suspend fun getAiTools(): Resource<List<AiTools>> = aiToolsDataSource.getAiTools()

    override suspend fun getModels(): Resource<List<String>> =
        dataSourceProvider.getDataSource().getModels()


    override suspend fun getSelectedAiModel(): Resource<String> =
        settingsDataSource.getSelectedAiModel()

    override suspend fun getSelectedAiTool(): Resource<AiTools> =
        settingsDataSource.getSelectedAiTool()


    override suspend fun getReadAloud(): Resource<Boolean> = settingsDataSource.getReadAloud()

    override suspend fun toggleReadAloud(isOn: Boolean) = settingsDataSource.toggleReadAloud(isOn)

    override suspend fun chooseAiModel(model: String) = settingsDataSource.chooseAiModel(model)

    override suspend fun chooseAiTool(tool: AiTools) = settingsDataSource.chooseAiTool(tool)

    override suspend fun getUserId(): Resource<String> = settingsDataSource.getUserId()

    override suspend fun getDefaultChatModel(): String = settingsDataSource.getDefaultChatModel()

}
