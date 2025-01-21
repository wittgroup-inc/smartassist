package com.gowittgroup.smartassistlib.util


import com.gowittgroup.smartassistlib.data.datasources.ai.AiDataSource
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.data.di.CHAT_GPT
import com.gowittgroup.smartassistlib.data.di.GEMINI
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.util.Constants.DEFAULT_AI_TOOL
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AiDataSourceProvider @Inject constructor(@Named(CHAT_GPT) private val chatGpt: AiDataSource,
                                               @Named(GEMINI) private val gemini: AiDataSource, private val settingsDataSource: SettingsDataSource
) {
     suspend fun getDataSource(): AiDataSource =
        when(settingsDataSource.getSelectedAiTool().successOr(DEFAULT_AI_TOOL)){
            AiTools.NONE -> gemini
            AiTools.CHAT_GPT ->  chatGpt
            AiTools.GEMINI ->  gemini
        }
}