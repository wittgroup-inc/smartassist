package com.gowittgroup.smartassistlib

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.di.CHAT_GPT
import com.gowittgroup.smartassistlib.di.GEMINI
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.successOr
import javax.inject.Inject
import javax.inject.Named

class AiDataSourceProvider @Inject constructor(@Named(CHAT_GPT) private val chatGpt: AiDataSource,
                         @Named(GEMINI) private val gemini: AiDataSource, private val settingsDataSource: SettingsDataSource) {
     suspend fun getDataSource(): AiDataSource =
        when(settingsDataSource.getSelectedAiTool().successOr(AiTools.CHAT_GPT)){
            AiTools.NONE -> chatGpt
            AiTools.CHAT_GPT ->  chatGpt
            AiTools.GEMINI ->  gemini
        }
}