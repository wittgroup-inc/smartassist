package com.gowittgroup.smartassistlib.data.repositories.ai

import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.repositories.ai.AnswerRepository
import com.gowittgroup.smartassistlib.models.ai.Message
import com.gowittgroup.smartassistlib.util.AiDataSourceProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnswerRepositoryImpl @Inject constructor(
    private val dataSourceProvider: AiDataSourceProvider
    ) : AnswerRepository {
    override suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>> = dataSourceProvider.getDataSource().getReply(query.map(::toMessage))

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

}
