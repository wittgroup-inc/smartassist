package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.datasources.SettingsDataSource
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.di.CHAT_GPT
import com.gowittgroup.smartassistlib.di.GEMINI
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Message
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import com.gowittgroup.smartassistlib.models.successOr
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class AnswerRepositoryImpl @Inject constructor(
    @Named(CHAT_GPT) private val chatGpt: AiDataSource,
    @Named(GEMINI) private val gemini: AiDataSource,
    private val settingsDataSource: SettingsDataSource
    ) : AnswerRepository {
    override suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>> = getDataSource().getReply(query.map(::toMessage))

    private fun toMessage(conversation: Conversation): Message = with(conversation) {
        Message(
            role =
            if (forSystem) {
                Message.ROLE_SYSTEM
            } else if (isQuestion) {
                Message.ROLE_USER
            } else {
                Message.ROLE_ASSISTANT
            }, content = data
        )
    }

    private suspend fun getDataSource():AiDataSource  =
        when(settingsDataSource.getSelectedAiTool().successOr(AiTools.CHAT_GPT)){
            AiTools.NONE -> chatGpt
            AiTools.CHAT_GPT ->  chatGpt
            AiTools.GEMINI ->  gemini
        }
}
