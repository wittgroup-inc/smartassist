import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.data.datasources.ai.ChatGpt
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.ai.Message
import com.gowittgroup.smartassistlib.util.KeyManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ChatGptTest : BaseTest() {

    private lateinit var chatGpt: ChatGpt
    private var mockSettingsDataSource: SettingsDataSource = mockk(relaxed = true)
    private var mockKeyManager: KeyManager = mockk(relaxed = true)
    private var mockOkHttpClient: OkHttpClient = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        chatGpt = spyk(
            ChatGpt(
                mockSettingsDataSource, mockKeyManager
            )
        )
        mockkStatic(SmartLog::class)
    }

    @Test
    fun `test getModels returns default model`() = runTest {
        val defaultModel = "gpt-3.5-turbo"
        coEvery { mockSettingsDataSource.getDefaultChatModel() } returns defaultModel

        val result = chatGpt.getModels()

        assertTrue(result is Resource.Success)
        assertEquals(listOf(defaultModel), (result as Resource.Success).data)
        coVerify { mockSettingsDataSource.getDefaultChatModel() }
    }

    @Test
    fun `test getReply processes response correctly`() = runTest {
        val selectedModel = "gpt-4"
        val userId = "user-123"
        val messages = listOf(Message(role = "user", content = "Hi!"))


        coEvery { mockSettingsDataSource.getSelectedAiModel() } returns Resource.Success(
            selectedModel
        )
        coEvery { mockSettingsDataSource.getUserId() } returns Resource.Success(userId)


        every { mockKeyManager.getOpenAiKey() } returns "test-api-key"


        val result = chatGpt.getReply(messages)


        coVerify { mockSettingsDataSource.getSelectedAiModel() }
        coVerify { mockSettingsDataSource.getUserId() }
        verify { mockKeyManager.getOpenAiKey() }
    }


}
