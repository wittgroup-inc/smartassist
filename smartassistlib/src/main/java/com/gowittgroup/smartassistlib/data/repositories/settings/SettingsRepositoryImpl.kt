package com.gowittgroup.smartassistlib.data.repositories.settings

import com.gowittgroup.smartassistlib.data.datasources.ai.AiToolsDataSource
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.settings.SettingsRepository
import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.util.AiDataSourceProvider
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

    override suspend fun chooseAiTool(tool: AiTools)  {
        settingsDataSource.chooseAiTool(tool).also {
            chooseAiModel(tool.defaultModel)
        }
    }

    override suspend fun getUserId(): Resource<String> = settingsDataSource.getUserId()

    override suspend fun getDefaultChatModel(): String = settingsDataSource.getDefaultChatModel()
    override suspend fun toggleHandsFreeMode(isOn: Boolean) {
        settingsDataSource.toggleHandsFreeMode(isOn)
        settingsDataSource.toggleReadAloud(isOn)
    }

    override suspend fun getHandsFreeMode(): Resource<Boolean> = settingsDataSource.getHandsFreeMode()
    override suspend fun setUserSubscriptionStatus(active: Boolean) {
        settingsDataSource.setUserSubscriptionStatus(active)
    }

    override suspend fun getUserSubscriptionStatus(): Resource<Boolean> = settingsDataSource.getUserSubscriptionStatus()

}
