package com.gowittgroup.smartassist.ui.homescreen

import androidx.lifecycle.SavedStateHandle
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.datasources.AiDataSource
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.models.AiTools
import com.gowittgroup.smartassistlib.models.Message
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.StreamResource
import com.gowittgroup.smartassistlib.repositories.AnswerRepository
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HomeViewModelTest {

    lateinit var viewModel: HomeViewModel



    @BeforeAll
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setup(){
        Dispatchers.setMain(Dispatchers.Unconfined)
        val historyRepository: ConversationHistoryRepository = FakeConversationHistoryRepository()
        val answerRepository:AnswerRepository = FakeAnswerRepository()
        val settingsRepository: SettingsRepository = FakeSettingRepository()
        val translations: HomeScreenTranslations = FakeHomeScreenTranslations()
        val networkUtil: NetworkUtil = FakeNetworkUtil(true)
        val handle = SavedStateHandle

        viewModel = HomeViewModel(
            answerRepository = answerRepository,
            settingsRepository = settingsRepository,
            historyRepository = historyRepository,
            translations = translations,
            networkUtil = networkUtil,
            savedStateHandle = handle.createHandle(null, null),
        )
    }
    @Test
    fun evenTest() {
        assertEquals("EVEN", viewModel.oddEven(10))
    }

    @Test
    fun oddTest() {
        assertEquals("ODD", viewModel.oddEven(17))
    }
}

class FakeNetworkUtil(private val isOnline: Boolean) : NetworkUtil {
    override fun isDeviceOnline(): Boolean {
        TODO("Not yet implemented")
    }

}

class FakeHomeScreenTranslations :
    HomeScreenTranslations {
    override fun noInternetConnectionMessage(): String {
        TODO("Not yet implemented")
    }

    override fun unableToGetReply(): String {
        TODO("Not yet implemented")
    }

    override fun listening(): String {
        TODO("Not yet implemented")
    }

    override fun tapAndHoldToSpeak(): String {
        TODO("Not yet implemented")
    }

}

class FakeConversationHistoryRepository : ConversationHistoryRepository {
    override suspend fun getConversationHistory(): Resource<Flow<List<ConversationHistory>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getConversationById(id: Long): Resource<ConversationHistory> {
        TODO("Not yet implemented")
    }

    override suspend fun saveConversationHistory(conversationHistory: ConversationHistory) {
        TODO("Not yet implemented")
    }

    override suspend fun clearConversationHistory(conversationHistory: ConversationHistory) {
        TODO("Not yet implemented")
    }

}


class FakeDataSource : AiDataSource {
    override suspend fun getModels(): Resource<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getReply(message: List<Message>): Resource<Flow<StreamResource<String>>> {
        TODO("Not yet implemented")
    }

}

class FakeAnswerRepository : AnswerRepository {

    override suspend fun getReply(query: List<Conversation>): Resource<Flow<StreamResource<String>>> {
        TODO("Not yet implemented")
    }


}

class FakeSettingRepository: SettingsRepository {
    override suspend fun getAiTools(): Resource<List<AiTools>> {
        return Resource.Success(AiTools.values().toList())
    }

    override suspend fun getModels(): Resource<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSelectedAiModel(): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getSelectedAiTool(): Resource<AiTools> {
        TODO("Not yet implemented")
    }

    override suspend fun getReadAloud(): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleReadAloud(isOn: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun chooseAiModel(chatModel: String) {
        TODO("Not yet implemented")
    }

    override suspend fun chooseAiTool(tool: AiTools) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserId(): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getDefaultChatModel(): String {
        TODO("Not yet implemented")
    }

    override suspend fun toggleHandsFreeMode(isOn: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun getHandsFreeMode(): Resource<Boolean> {
        TODO("Not yet implemented")
    }


}