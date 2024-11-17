package com.gowittgroup.smartassistlib.data.datasources.ai

import BaseTest
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.AiTools
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AiToolsDataSourceImplTest: BaseTest() {

    private val aiToolsDataSource = AiToolsDataSourceImpl()

    @Test
    fun `getAiTools should return a success resource with a list of AiTools`() = runTest {
        // Act
        val result = aiToolsDataSource.getAiTools()

        // Assert
        assert(result is Resource.Success)
        assertEquals(AiTools.entries, (result as Resource.Success).data)
    }
}