package com.gowittgroup.smartassistlib.repositories

import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.models.Message
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnswerRepositoryImpl @Inject constructor(private val aiDataSource: AiDataSource) : AnswerRepository {
    override suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>> = aiDataSource.getReply(query.map(::toMessage))

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
