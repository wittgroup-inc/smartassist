package com.gowittgroup.smartassist.ui.homescreen

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.services.speechrecognizer.SmartSpeechRecognitionCallbacks
import com.gowittgroup.smartassist.services.speechrecognizer.SmartSpeechRecognizer
import com.gowittgroup.smartassist.services.textospeech.SmartTextToSpeech
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.Banner
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassist.ui.homescreen.components.BackPress
import com.gowittgroup.smartassist.ui.homescreen.components.ChatBarSection
import com.gowittgroup.smartassist.ui.homescreen.components.ConversationSection
import com.gowittgroup.smartassist.ui.homescreen.components.HandsFreeModeNotification
import com.gowittgroup.smartassist.ui.homescreen.components.HandsFreeModeSection
import com.gowittgroup.smartassist.ui.homescreen.components.NewChatFloatingButton
import com.gowittgroup.smartassist.ui.homescreen.components.Notification
import com.gowittgroup.smartassist.ui.homescreen.components.TopBarSection
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassist.util.isAndroidTV
import com.gowittgroup.smartassistlib.models.ai.AiTools
import kotlinx.coroutines.launch


private const val TAG = "HomeScreen"

private val COMMAND_VARIATION = listOf("ok buddy", "okay buddy")
private const val SPEECH_RECOGNIZER_MAX_RETRY_TIME: Long = 5 * 60 * 1000


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    isExpanded: Boolean,
    showTopAppBar: Boolean,
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToPrompts: () -> Unit,
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToSummarize: () -> Unit,
    smartAnalytics: SmartAnalytics,
    ask: (query: String?, speak: (String) -> Unit) -> Unit,
    beginningSpeech: () -> Unit,
    setCommandModeAfterReply: (setCommandMode: () -> Unit) -> Unit,
    handsFreeModeStopListening: (stopRecognizer: () -> Unit) -> Unit,
    setCommandMode: (setCommandMode: () -> Unit) -> Unit,
    releaseCommandMode: (releaseCommandMode: () -> Unit) -> Unit,
    handsFreeModeStartListening: (startRecognizer: () -> Unit) -> Unit,
    resetErrorMessage: () -> Unit,
    setReadAloud: (isOn: Boolean) -> Unit,
    closeHandsFreeAlert: () -> Unit,
    setHandsFreeMode: () -> Unit,
    stopListening: (stopRecognizer: () -> Unit) -> Unit,
    startListening: (startRecognizer: () -> Unit) -> Unit,
    updateHint: (hint: String) -> Unit,
    refreshAll: () -> Unit
) {

    SmartLog.d(TAG, "Enter home")

    val context: Context = LocalContext.current
    val density = LocalDensity.current

    val textToSpeech: MutableState<SmartTextToSpeech> = remember(context) {
        mutableStateOf(SmartTextToSpeech().apply { initialize(context) })
    }

    val speechRecognizerHoldAndSpeak: SmartSpeechRecognizer = remember(context) {
        SmartSpeechRecognizer().apply { initialize(context) }
    }

    val speechRecognizerHandsFree: SmartSpeechRecognizer = remember(context) {
        SmartSpeechRecognizer().apply { initialize(context) }
    }

    val speechRecognizerHandsFreeCommand: SmartSpeechRecognizer = remember(context) {
        SmartSpeechRecognizer().apply { initialize(context) }
    }

    var showHandsFreeAlertDialog by remember { mutableStateOf(false) }

    var chatBarHeight by remember { mutableIntStateOf(0) }

    var showBanner by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    showError = uiState.error.value.isNotEmpty()

    initSpeechRecognizerForHandsFree(
        speechRecognizerHandsFree = speechRecognizerHandsFree,
        handsFreeMode = uiState.handsFreeMode.value,
        smartAnalytics = smartAnalytics,
        commandRecognizer = speechRecognizerHandsFreeCommand,
        ask = { query ->
            ask(query) { content ->
                speak(content = content, textToSpeech.value)
            }
        },
        beginningSpeech = beginningSpeech,
        setCommandModeAfterReply = { func -> setCommandModeAfterReply(func) },
        handsFreeModeStopListening = { func -> handsFreeModeStopListening(func) }
    )

    intSpeechRecognizerForHandsFreeCommand(
        commandRecognizer = speechRecognizerHandsFreeCommand,
        queryRecognizer = speechRecognizerHandsFree,
        setCommandMode = { func -> setCommandMode(func) },
        releaseCommandMode = { func -> releaseCommandMode(func) },
        handsFreeModeStartListening = { func -> handsFreeModeStartListening(func) }
    )

    initSpeechRecognizerForHoldAndSpeak(
        speechRecognizer = speechRecognizerHoldAndSpeak,
        smartAnalytics = smartAnalytics,
        ask = { query ->
            ask(query) { content ->
                speak(content = content, textToSpeech.value)
            }
        },
        beginningSpeech = beginningSpeech
    )

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val fabVisibility by remember {
        derivedStateOf {
            val isLastItemVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.let {
                it.index == listState.layoutInfo.totalItemsCount - 1 && it.offset == 0
            } ?: false
            listState.firstVisibleItemIndex == 0 || isLastItemVisible
        }

    }

    logUserEntersEvent(smartAnalytics)

    val conversations = uiState.conversations.filter { !it.forSystem }

    LaunchedEffect(
        key1 = uiState.handsFreeMode.value,
        key2 = uiState.showHandsFreeAlertIsClosed
    ) {
        showHandsFreeAlertDialog =
            context.isAndroidTV() && !uiState.handsFreeMode.value && !uiState.showHandsFreeAlertIsClosed
    }

    LaunchedEffect(key1 = uiState.handsFreeMode.value) {
        if (uiState.handsFreeMode.value) {
            SmartLog.d(TAG, "Calling to setCommand")
            setCommandMode { speechRecognizerHandsFreeCommand.startListening() }
        } else {
            SmartLog.d(TAG, "Calling to releaseCommand")
            releaseCommandMode { speechRecognizerHandsFreeCommand.stopListening() }
            handsFreeModeStopListening { speechRecognizerHandsFree.stopListening() }
        }
    }

    LaunchedEffect(key1 = true) {
        SmartLog.d(TAG, "Screen refreshed")
        refreshAll()

        val position = conversations.size - 1
        if (position in conversations.indices) {
            listState.scrollToItem(position)
        }
    }

    LaunchedEffect(key1 = uiState.banner.showBanner, key2 = !Session.userHasClosedTheBanner) {
        showBanner = uiState.banner.showBanner && !Session.userHasClosedTheBanner
    }


    DisposableEffect(Unit) {
        onDispose {
            SmartLog.d(TAG, "Disposing resources")
            shutdownTextToSpeech(textToSpeech.value)
            shutdownSpeechRecognizer(speechRecognizerHoldAndSpeak)
            shutdownSpeechRecognizer(speechRecognizerHandsFree)
            shutdownSpeechRecognizer(speechRecognizerHandsFreeCommand)
        }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    BackPress()
    Scaffold(
        topBar = {
            when {
                showError -> Notification(
                    onNotificationClose = {
                        resetErrorMessage()
                        showError = false
                    },
                    notificationState = NotificationState(
                        message = uiState.error.value,
                        type = NotificationType.ERROR
                    )
                )

                showHandsFreeAlertDialog -> HandsFreeModeNotification(
                    message = stringResource(R.string.hands_free_alert_dialog_message),
                    onCancel = {
                        showHandsFreeAlertDialog = false
                        closeHandsFreeAlert()
                    },
                    onOk = {
                        setHandsFreeMode()
                        showHandsFreeAlertDialog = false
                    })

                showBanner -> uiState.banner.content?.let {
                    Banner(banner = it, onClose = {
                        showBanner = false
                        Session.userHasClosedTheBanner = true
                    })
                }

                else -> TopBarSection(
                    uiState = uiState,
                    onSpeakerIconClick = { isOn -> setReadAloud(isOn) },
                    textToSpeech = textToSpeech,
                    context = context,
                    navigateToSettings = navigateToSettings,
                    openDrawer = openDrawer,
                    topAppBarState = topAppBarState,
                    scrollBehavior = scrollBehavior,
                    isExpanded = isExpanded
                )
            }
        },

        floatingActionButton = {
            if (fabVisibility && uiState.conversations.isNotEmpty()) {
                NewChatFloatingButton(
                    modifier = Modifier.padding(bottom = with(density) { chatBarHeight.toDp() }),
                    navigateToHome = navigateToHome
                )
            }
        },

        floatingActionButtonPosition = FabPosition.End,

        content = { innerPaddings ->
            Column(modifier = modifier
                .padding(top = innerPaddings.calculateTopPadding())
                .windowInsetsPadding(WindowInsets.systemBars)
                .windowInsetsPadding(WindowInsets.ime)
            ) {
                ConversationSection(
                    conversations = conversations,
                    modifier = modifier.weight(1f),
                    navigateToHistory = navigateToHistory,
                    navigateToPrompts = navigateToPrompts,
                    navigateToSubscription = navigateToSubscription,
                    navigateToSummarize = navigateToSummarize,
                    context = context,
                    listState = listState
                )

                if (uiState.handsFreeMode.value) {
                    HandsFreeModeSection(uiState)
                } else {
                    val speakHint = stringResource(id = R.string.tap_and_hold_to_speak)
                    val typeHint = stringResource(id = R.string.startTyping)
                    ChatBarSection(
                        uiState = uiState,
                        modifier = modifier.onGloballyPositioned { coordinates ->
                            chatBarHeight = coordinates.size.height
                        },
                        onSend = {
                            sendQuery(
                                send = {
                                    ask(null) { content ->
                                        speak(
                                            content = content,
                                            textToSpeech = textToSpeech.value
                                        )
                                    }
                                },
                                isVoiceMessage = false,
                                smartAnalytics = smartAnalytics
                            )

                        },
                        onActionUp = {
                            updateHint(typeHint)
                            stopListening { speechRecognizerHoldAndSpeak.stopListening() }
                        },
                        onActionDown = {
                            updateHint(speakHint)
                            startListening {
                                speechRecognizerHoldAndSpeak.startListening()
                            }
                        }
                    )
                }

                SideEffect {
                    coroutineScope.launch {
                        val position = conversations.size - 1
                        if (position in conversations.indices) {
                            listState.animateScrollToItem(position)
                        }
                    }
                }
            }
        })
}

fun prepareContent(conversations: List<Conversation>): StringBuilder {
    val shareText = StringBuilder("----------------------- Chat History -----------------------")
    shareText.appendLine()
    shareText.appendLine()
    conversations.forEach { conversation ->
        if (conversation.forSystem) return@forEach
        if (conversation.isQuestion) {
            shareText.append("You:")
        } else {
            when (conversation.replyFrom) {
                AiTools.NONE -> shareText.append("Bot:")
                AiTools.CHAT_GPT -> shareText.append("ChatGPT:")
                AiTools.GEMINI -> shareText.append("Gemini:")
            }
        }

        shareText.append(" ")
        shareText.append(conversation.data)
        shareText.appendLine()
        shareText.appendLine()
    }
    return shareText
}


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "home_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}

private fun logSendMessageEvent(smartAnalytics: SmartAnalytics, isVoiceMessage: Boolean) {
    val bundle = Bundle()
    bundle.putString(
        SmartAnalytics.Param.ITEM_NAME,
        if (isVoiceMessage) "voice_message" else "text_message"
    )
    smartAnalytics.logEvent(SmartAnalytics.Event.SEND_MESSAGE, bundle)
}


private fun speak(content: String, textToSpeech: SmartTextToSpeech?) {
    textToSpeech?.speak(content)
}


private fun initSpeechRecognizer(
    speechRecognizer: SmartSpeechRecognizer,
    onBeginSpeech: () -> Unit,
    onResult: (String) -> Unit,
    retryDuration: Long,
    isCommand: Boolean = false
) {


    speechRecognizer.setCallback(object : SmartSpeechRecognitionCallbacks() {
        var tag = if (isCommand) "COMMAND: " else "QUERY: "

        val handler = Handler(Looper.getMainLooper())

        var shouldRetry = true

        init {
            handler.postDelayed({
                shouldRetry = false
            }, retryDuration)
        }

        override fun onBeginningOfSpeech() {
            SmartLog.d(TAG, "$tag onBeginningOfSpeech()")
            onBeginSpeech()
        }

        override fun onError(error: Int) {
            SmartLog.d(TAG, "$tag Error $error")
            if (error == 7 && shouldRetry && speechRecognizer.isListening) {
                speechRecognizer.startListening()
            }
        }

        override fun onResults(results: List<String>) {
            SmartLog.d(TAG, "$tag ${results.firstOrNull()}")
            onResult(results.firstOrNull() ?: "")
        }

        override fun onEndOfSpeech() {
            SmartLog.d(TAG, "$tag End of speech")
        }
    })
}

private fun initSpeechRecognizerForHoldAndSpeak(
    speechRecognizer: SmartSpeechRecognizer,
    smartAnalytics: SmartAnalytics,
    ask: (String) -> Unit,
    beginningSpeech: () -> Unit
) {
    SmartLog.d(TAG, "Initializing SpeechRecognizerForHoldAndSpeak")
    initSpeechRecognizer(
        speechRecognizer = speechRecognizer,
        onResult = { query ->
            SmartLog.d(TAG, "Received voice query: $query")
            sendQuery({
                ask(query)
            }, isVoiceMessage = true, smartAnalytics = smartAnalytics)
        },
        onBeginSpeech = beginningSpeech,
        retryDuration = 0
    )
}

private fun initSpeechRecognizerForHandsFree(
    speechRecognizerHandsFree: SmartSpeechRecognizer,
    handsFreeMode: Boolean,
    smartAnalytics: SmartAnalytics,
    commandRecognizer: SmartSpeechRecognizer,
    ask: (String) -> Unit,
    beginningSpeech: () -> Unit,
    setCommandModeAfterReply: (start: () -> Unit) -> Unit,
    handsFreeModeStopListening: (stop: () -> Unit) -> Unit
) {
    SmartLog.d(TAG, "Initializing SpeechRecognizerForHandsFree")
    initSpeechRecognizer(
        speechRecognizer = speechRecognizerHandsFree,
        onResult = { query ->
            SmartLog.d(TAG, "Received voice query: $query")

            if (handsFreeMode) {
                handsFreeModeStopListening { speechRecognizerHandsFree.stopListening() }
            }

            sendQuery({
                ask(query)
            }, isVoiceMessage = true, smartAnalytics = smartAnalytics)

            if (handsFreeMode) {
                setCommandModeAfterReply {
                    SmartLog.d(TAG, "Calling to setCommand")
                    commandRecognizer.startListening()
                }
            }
        },
        onBeginSpeech = beginningSpeech,
        retryDuration = 1 * 60 * 1000
    )
}

private fun intSpeechRecognizerForHandsFreeCommand(
    commandRecognizer: SmartSpeechRecognizer,
    queryRecognizer: SmartSpeechRecognizer,
    setCommandMode: (star: () -> Unit) -> Unit,
    releaseCommandMode: (stop: () -> Unit) -> Unit,
    handsFreeModeStartListening: (start: () -> Unit) -> Unit
) {
    SmartLog.d(TAG, "Initializing SpeechRecognizerForHandsFreeCommand")
    initSpeechRecognizer(
        isCommand = true,
        speechRecognizer = commandRecognizer,
        onResult = { command ->
            SmartLog.d(TAG, "Received Command: $command")
            releaseCommandMode { commandRecognizer.stopListening() }
            if (COMMAND_VARIATION.contains(command.lowercase())) {
                SmartLog.d(TAG, "Command Accepted")
                handsFreeModeStartListening {
                    SmartLog.d(TAG, "Started Listening")
                    queryRecognizer.startListening()
                }
            } else {
                SmartLog.d(TAG, "Calling to setCommand")
                setCommandMode {
                    SmartLog.d(TAG, "Command Rejected")
                    commandRecognizer.startListening()
                }
            }
        },
        onBeginSpeech = { },
        retryDuration = SPEECH_RECOGNIZER_MAX_RETRY_TIME
    )
}

private fun shutdownSpeechRecognizer(speechRecognizer: SmartSpeechRecognizer) {
    try {
        speechRecognizer.shutDown()
    } catch (e: Exception) {
        SmartLog.d(TAG, "Unable to destroy speechRecognizer.")
    }
}

fun shutdownTextToSpeech(textToSpeech: SmartTextToSpeech) {
    SmartLog.d(TAG, "Stopping text to speech instance: $textToSpeech")
    textToSpeech.shutdown()
}

private fun sendQuery(
    send: () -> Unit,
    smartAnalytics: SmartAnalytics,
    isVoiceMessage: Boolean
) {
    send()
    logSendMessageEvent(smartAnalytics, isVoiceMessage)
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        uiState = HomeUiState.DEFAULT,
        isExpanded = false,
        showTopAppBar = true,
        openDrawer = { },
        navigateToSettings = { },
        navigateToHistory = { },
        navigateToPrompts = { },
        navigateToHome = { _, _ -> },
        smartAnalytics = FakeAnalytics(),
        ask = { _, _ -> },
        beginningSpeech = { },
        setCommandModeAfterReply = {},
        handsFreeModeStopListening = {},
        setCommandMode = {},
        releaseCommandMode = {},
        handsFreeModeStartListening = {},
        resetErrorMessage = { },
        setReadAloud = {},
        closeHandsFreeAlert = { },
        setHandsFreeMode = { },
        stopListening = {},
        startListening = {},
        updateHint = {},
        refreshAll = {},
        navigateToSubscription = {},
        navigateToSummarize = {}
    )
}

