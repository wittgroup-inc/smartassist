package com.gowittgroup.smartassistlib.datasources

import android.content.res.Resources
import com.gowittgroup.smartassistlib.models.Prompts
import com.gowittgroup.smartassistlib.models.PromptsCategory
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class PromptsDataSourceImpl : PromptsDataSource {
    override suspend fun getPromptsCategories(): Resource<Flow<List<PromptsCategory>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPromptsForCategory(categoryId: Long): Resource<Flow<Prompts>> {
        TODO("Not yet implemented")
    }


    override suspend fun getAllPrompts(): Resource<Flow<List<Prompts>>> {
        val result = MutableSharedFlow<List<Prompts>>(1)
        val none = PromptsCategory(1, "None", "Anything")
        val nonePrompts = Prompts(none, listOf("Hello", "How are you doing today?", "What you can do for me?"))
        val productManager = PromptsCategory(1, "Product Manager", "Prompts around your product design")
        val productManagerPrompts = Prompts(
            productManager,
            listOf(
                "I want to develop a clinical product that will create doctors appointments. Write product description.",
                "I want to develop a clinical product that will create doctors appointments. Write product revenue generation."
            )
        )
        result.emit(listOf(nonePrompts, productManagerPrompts))
        return Resource.Success(result)
    }

}
