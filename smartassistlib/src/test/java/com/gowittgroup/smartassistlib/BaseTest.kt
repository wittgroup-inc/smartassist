import io.mockk.MockKAnnotations.init
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
open class BaseTest {

    protected val testDispatcher = StandardTestDispatcher()
    protected val testScope = TestScope(testDispatcher)

    @BeforeEach
    fun setUpBase() {
        init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDownBase() {
        Dispatchers.resetMain()
        testScope.cancel()
    }
}