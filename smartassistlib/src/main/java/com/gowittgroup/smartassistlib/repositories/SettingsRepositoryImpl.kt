package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.AiToolsDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.di.CHAT_GPT
import com.gowittgroup.smartassistlib.di.GEMINI
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.successOr
import javax.inject.Inject
import javax.inject.Named

class SettingsRepositoryImpl @Inject constructor(@Named(CHAT_GPT) private val chatGpt: AiDataSource,
                                                 @Named(GEMINI) private val gemini: AiDataSource,
                                                 private val settingsDataSource: SettingsDataSource,
                                                 private val aiToolsDataSource: AiToolsDataSource) : SettingsRepository {
    override suspend fun getAiTools(): Resource<List<AiTools>> = aiToolsDataSource.getAiTools()

    override suspend fun getModels(): Resource<List<String>> = getDataSource().getModels()

    private suspend fun getDataSource():AiDataSource  =
       when(getSelectedAiTool().successOr(AiTools.CHAT_GPT)){
            AiTools.NONE -> chatGpt
            AiTools.CHAT_GPT ->  chatGpt
            AiTools.GEMINI ->  gemini
    }


    override suspend fun getSelectedAiModel(): Resource<String> = settingsDataSource.getSelectedAiModel()

    override suspend fun getSelectedAiTool(): Resource<AiTools> = settingsDataSource.getSelectedAiTool()


    override suspend fun getReadAloud(): Resource<Boolean> = settingsDataSource.getReadAloud()

    override suspend fun toggleReadAloud(isOn: Boolean) = settingsDataSource.toggleReadAloud(isOn)

    override suspend fun chooseAiModel(model: String) = settingsDataSource.chooseAiModel(model)

    override suspend fun chooseAiTool(tool: AiTools)  = settingsDataSource.chooseAiTool(tool)

    override suspend fun getUserId(): Resource<String> = settingsDataSource.getUserId()
    override suspend fun getDefaultChatModel(): String = settingsDataSource.getDefaultChatModel()

}
