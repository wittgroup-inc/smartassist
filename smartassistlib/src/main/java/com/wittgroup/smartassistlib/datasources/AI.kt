package com.wittgroup.smartassistlib.datasources

import com.wittgroup.smartassistlib.models.Resource

interface AI {
    suspend fun getModels(): Resource<List<String>>
    suspend fun getAnswer(query: String): Resource<String>
}
