package com.wittgroup.smartassistlib.repositories

import com.wittgroup.smartassistlib.models.Resource

interface AnswerRepository {
    suspend fun getAnswer(query: String): Resource<String>
}

