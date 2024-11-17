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
        // Mocking dependencies with MockK
        mockSettingsDataSource = mockk()
        mockKeyManager = mockk()

        // Initialize Gemini with mocked dependencies
        gemini = Gemini(
            settingsDataSource = mockSettingsDataSource,
            context = mockk(),  // Mock context if needed, it won't be used in this test.
            keyManager = mockKeyManager
        )
    }

    @Test
    fun `test getReply processes response correctly`() = runTest {
        val selectedModel = "gemini-4"
        val messages = listOf(Message(role = "user", content = "Hello, Gemini!"))

        // Mocking the SettingsDataSource to return a valid AI model
        coEvery { mockSettingsDataSource.getSelectedAiModel() } returns Resource.Success(selectedModel)
        coEvery { mockSettingsDataSource.getDefaultChatModel() } returns selectedModel
        coEvery { mockKeyManager.getGeminiKey() } returns "test-api-key"

        // Simulate the successful flow returned by the sendRequest function
        val responseFlow = flowOf(StreamResource.StreamStarted(""))


        // Call the method and collect the result
        val result = gemini.getReply(messages)


        // Check that the result is as expected

        // Verify that the SettingsDataSource and KeyManager methods were called
        coVerify { mockSettingsDataSource.getSelectedAiModel() }
        verify { mockKeyManager.getGeminiKey() }
    }

    @Test
    fun `test getReply handles error correctly`() = runTest {
        val messages = listOf(Message(role = "user", content = "Hello, Gemini!"))

        // Call the method and verify the error handling
        val result = gemini.getReply(messages)


        // Verify that the SettingsDataSource method was called
        coVerify { mockSettingsDataSource.getSelectedAiModel() }
    }

    @Test
    fun `test getReply handles streaming response`() = runTest {
        val messages = listOf(Message(role = "user", content = "Hello, Gemini!"))

        // Mock the SettingsDataSource to return a valid AI model and mock the key manager
        coEvery { mockSettingsDataSource.getSelectedAiModel() } returns Resource.Success("gemini-4")
        coEvery { mockSettingsDataSource.getDefaultChatModel() } returns "gemini-4"
        coEvery { mockKeyManager.getGeminiKey() } returns "test-api-key"

        // Mock the sendRequest function to emit a sequence of StreamResource items
        val responseFlow = flowOf(
            StreamResource.StreamStarted(""),
            StreamResource.StreamInProgress("Hello"),
            StreamResource.StreamCompleted(true)
        )

        // Call the method and collect the result
        val result = gemini.getReply(messages)

        // Verify that the SettingsDataSource and KeyManager methods were called
        coVerify { mockSettingsDataSource.getSelectedAiModel() }
        verify { mockKeyManager.getGeminiKey() }
    }
}
