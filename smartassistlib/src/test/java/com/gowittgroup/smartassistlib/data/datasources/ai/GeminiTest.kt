import com.gowittgroup.smartassistlib.data.datasources.ai.Gemini
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.models.ai.Message
import com.gowittgroup.smartassistlib.util.KeyManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GeminiTest {

    private lateinit var gemini: Gemini
    private lateinit var mockSettingsDataSource: SettingsDataSource
    private lateinit var mockKeyManager: KeyManager

    @BeforeEach
    fun setUp() {

        mockSettingsDataSource = mockk()
        mockKeyManager = mockk()


        gemini = Gemini(
            settingsDataSource = mockSettingsDataSource,
            context = mockk(),
            keyManager = mockKeyManager
        )
    }

    @Test
    fun `test getReply processes response correctly`() = runTest {
        val selectedModel = "gemini-4"
        val messages = listOf(Message(role = "user", content = "Hello, Gemini!"))


        coEvery { mockSettingsDataSource.getSelectedAiModel() } returns Resource.Success(selectedModel)
        coEvery { mockSettingsDataSource.getDefaultChatModel() } returns selectedModel
        coEvery { mockKeyManager.getGeminiKey() } returns "test-api-key"


        val responseFlow = flowOf(StreamResource.StreamStarted(""))



        val result = gemini.getReply(messages)





        coVerify { mockSettingsDataSource.getSelectedAiModel() }
        verify { mockKeyManager.getGeminiKey() }
    }

    @Test
    fun `test getReply handles error correctly`() = runTest {
        val messages = listOf(Message(role = "user", content = "Hello, Gemini!"))


        val result = gemini.getReply(messages)



        coVerify { mockSettingsDataSource.getSelectedAiModel() }
    }

    @Test
    fun `test getReply handles streaming response`() = runTest {
        val messages = listOf(Message(role = "user", content = "Hello, Gemini!"))


        coEvery { mockSettingsDataSource.getSelectedAiModel() } returns Resource.Success("gemini-4")
        coEvery { mockSettingsDataSource.getDefaultChatModel() } returns "gemini-4"
        coEvery { mockKeyManager.getGeminiKey() } returns "test-api-key"


        val responseFlow = flowOf(
            StreamResource.StreamStarted(""),
            StreamResource.StreamInProgress("Hello"),
            StreamResource.StreamCompleted(true)
        )


        val result = gemini.getReply(messages)


        coVerify { mockSettingsDataSource.getSelectedAiModel() }
        verify { mockKeyManager.getGeminiKey() }
    }
}
