package com.gowittgroup.smartassist.ui.homescreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.models.BackPress
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.services.speechrecognizer.SmartSpeechRecognitionCallbacks
import com.gowittgroup.smartassist.services.speechrecognizer.SmartSpeechRecognizer
import com.gowittgroup.smartassist.services.textospeech.SmartTextToSpeech
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.Banner
import com.gowittgroup.smartassist.ui.components.ChatBar
import com.gowittgroup.smartassist.ui.components.ConversationView
import com.gowittgroup.smartassist.ui.components.EmptyScreen
import com.gowittgroup.smartassist.ui.components.HandsFreeModeNotification
import com.gowittgroup.smartassist.ui.components.HomeAppBar
import com.gowittgroup.smartassist.ui.rememberContentPaddingForScreen
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassist.util.isAndroidTV
import com.gowittgroup.smartassist.util.share
import com.gowittgroup.smartassistlib.models.AiTools
import kotlinx.coroutines.delay
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

    Log.d(TAG, "Enter home")

    val context: Context = LocalContext.current

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

    var showHandsFreeAlertDialog by remember {
        mutableStateOf(false)
    }

    var showBanner by remember {
        mutableStateOf(false)
    }

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


    val contentPadding = rememberContentPaddingForScreen(
        additionalTop = if (showTopAppBar) 0.dp else 8.dp,
        excludeTop = showTopAppBar
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
            Log.d(TAG, "Calling to setCommand")
            setCommandMode { speechRecognizerHandsFreeCommand.startListening() }
        } else {
            Log.d(TAG, "Calling to releaseCommand")
            releaseCommandMode { speechRecognizerHandsFreeCommand.stopListening() }
            handsFreeModeStopListening { speechRecognizerHandsFree.stopListening() }
        }
    }

    LaunchedEffect(key1 = true) {
        Log.d(TAG, "Screen refreshed")
        refreshAll()
        //Scrolling on new message.
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
            Log.d(TAG, "Disposing resources")
            shutdownTextToSpeech(textToSpeech.value)
            shutdownSpeechRecognizer(speechRecognizerHoldAndSpeak)
            shutdownSpeechRecognizer(speechRecognizerHandsFree)
            shutdownSpeechRecognizer(speechRecognizerHandsFreeCommand)
        }
    }

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    BackPress()
    ErrorView(uiState.error).also { resetErrorMessage() }
    Scaffold(
        topBar = {
            TopBarSection(
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
            if (showHandsFreeAlertDialog) {
                HandsFreeModeNotification(
                    message = stringResource(R.string.hands_free_alert_dialog_message),
                    onCancel = {
                        showHandsFreeAlertDialog = false
                        closeHandsFreeAlert()
                    },
                    onOk = {
                        setHandsFreeMode()
                        showHandsFreeAlertDialog = false
                    })
            }

            if (showBanner) {
                uiState.banner.content?.let {
                    Banner(banner = it, onClose = {
                        showBanner = false
                        Session.userHasClosedTheBanner = true
                    })
                }
            }
        },

        floatingActionButton = {
            if (fabVisibility && uiState.conversations.isNotEmpty()) {
                NewChatFloatingButton(navigateToHome)
            }
        },

        floatingActionButtonPosition = FabPosition.End,

        content = { padding ->


            Column(modifier = modifier.padding(padding)) {
                ConversationSection(
                    conversations = conversations,
                    modifier = modifier.weight(1f),
                    navigateToHistory = navigateToHistory,
                    navigateToPrompts = navigateToPrompts,
                    listState = listState,
                    context = context
                )

                if (uiState.showLoading) {
                    // TODO: can be handle later
                }

                if (uiState.handsFreeMode.value) {
                    HandsFreeModeSection(uiState)
                } else {
                    val speakHint = stringResource(id = R.string.tap_and_hold_to_speak)
                    val typeHint = stringResource(id = R.string.startTyping)
                    ChatBarSection(
                        uiState = uiState,
                        modifier = modifier,
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

                //Scrolling on new message.
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

@Composable
private fun HandsFreeModeSection(uiState: HomeUiState) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.d(TAG, "${uiState.speechRecognizerState}")
        when (uiState.speechRecognizerState) {
            SpeechRecognizerState.Listening -> {
                Log.d(TAG, "HandsFreeMode Start listening")
                ComposeLottieAnimation()
            }


            SpeechRecognizerState.Command -> {

                val message = buildAnnotatedString {
                    append("Say ")
                    withStyle(
                        style = MaterialTheme.typography.titleMedium.toSpanStyle()
                            .copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )
                    ) {
                        append("Okay buddy")
                    }
                    append(", and then ask your query.")
                }
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarSection(
    uiState: HomeUiState,
    onSpeakerIconClick: (on: Boolean) -> Unit,
    textToSpeech: MutableState<SmartTextToSpeech>,
    context: Context,
    navigateToSettings: () -> Unit,
    openDrawer: () -> Unit,
    topAppBarState: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    isExpanded: Boolean
) {
    HomeAppBar(
        actions = {
            Menu(
                conversations = uiState.conversations,
                readAloudInitialValue = uiState.readAloud,
                onSpeakerIconClick = { isOn ->
                    onSpeakerIconClick(isOn)
                    if (isOn) {
                        shutdownTextToSpeech(textToSpeech.value)
                        textToSpeech.value = SmartTextToSpeech().apply { initialize(context) }
                    } else {
                        shutdownTextToSpeech(textToSpeech.value)
                    }
                },
                onShareIconClick = {
                    val shareText = prepareContent(uiState.conversations)
                    context.share(shareText.toString(), "Chat History", "Share With")
                },
                onSettingsIconClick = {
                    navigateToSettings()
                })
        }, openDrawer = openDrawer,
        topAppBarState = topAppBarState,
        scrollBehavior = scrollBehavior,
        isExpanded = isExpanded
    )
}

private fun prepareContent(conversations: List<Conversation>): StringBuilder {
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


@Composable
private fun ChatBarSection(
    uiState: HomeUiState,
    modifier: Modifier,
    onSend: () -> Unit,
    onActionUp: () -> Unit,
    onActionDown: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
    ) {
        ChatBar(state = uiState.textFieldValue,
            hint = uiState.hint,
            icon = if (uiState.micIcon) painterResource(R.drawable.ic_mic_on) else painterResource(
                R.drawable.ic_mic_off
            ),
            modifier = modifier.padding(16.dp),
            actionUp = onActionUp,
            actionDown = onActionDown,
            onClick = { onSend() }
        )
    }
}


@Composable
private fun ConversationSection(
    conversations: List<Conversation>,
    modifier: Modifier,
    navigateToHistory: () -> Unit,
    navigateToPrompts: () -> Unit,
    listState: LazyListState,
    context: Context
) {
    if (conversations.isEmpty()) {
        EmptyScreen(
            stringResource(R.string.empty_chat_screen_message),
            modifier,
            navigateToHistory = navigateToHistory,
            navigateToPrompts = navigateToPrompts
        )
    } else {
        ConversationView(
            modifier = modifier,
            list = conversations,
            listState = listState,
            onCopy = { text -> copyTextToClipboard(context, text) }
        )
    }
}

@Composable
fun ComposeLottieAnimation(modifier: Modifier = Modifier) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.listening_animation))

    LottieAnimation(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        clipToCompositionBounds = true,
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, context.getString(R.string.text_copied_msg), Toast.LENGTH_SHORT).show()
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
            Log.d(TAG, "$tag onBeginningOfSpeech()")
            onBeginSpeech()
        }

        override fun onError(error: Int) {
            Log.d(TAG, "$tag Error $error")
            if (error == 7 && shouldRetry && speechRecognizer.isListening) {
                speechRecognizer.startListening()
            }
        }

        override fun onResults(results: List<String>) {
            Log.d(TAG, "$tag ${results.firstOrNull()}")
            onResult(results.firstOrNull() ?: "")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "$tag End of speech")
        }
    })
}

private fun initSpeechRecognizerForHoldAndSpeak(
    speechRecognizer: SmartSpeechRecognizer,
    smartAnalytics: SmartAnalytics,
    ask: (String) -> Unit,
    beginningSpeech: () -> Unit
) {
    Log.d(TAG, "Initializing SpeechRecognizerForHoldAndSpeak")
    initSpeechRecognizer(
        speechRecognizer = speechRecognizer,
        onResult = { query ->
            Log.d(TAG, "Received voice query: $query")
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
    Log.d(TAG, "Initializing SpeechRecognizerForHandsFree")
    initSpeechRecognizer(
        speechRecognizer = speechRecognizerHandsFree,
        onResult = { query ->
            Log.d(TAG, "Received voice query: $query")

            if (handsFreeMode) {
                handsFreeModeStopListening { speechRecognizerHandsFree.stopListening() }
            }

            sendQuery({
                ask(query)
            }, isVoiceMessage = true, smartAnalytics = smartAnalytics)

            if (handsFreeMode) {
                setCommandModeAfterReply {
                    Log.d(TAG, "Calling to setCommand")
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
    Log.d(TAG, "Initializing SpeechRecognizerForHandsFreeCommand")
    initSpeechRecognizer(
        isCommand = true,
        speechRecognizer = commandRecognizer,
        onResult = { command ->
            Log.d(TAG, "Received Command: $command")
            releaseCommandMode { commandRecognizer.stopListening() }
            if (COMMAND_VARIATION.contains(command.lowercase())) {
                Log.d(TAG, "Command Accepted")
                handsFreeModeStartListening {
                    Log.d(TAG, "Started Listening")
                    queryRecognizer.startListening()
                }
            } else {
                Log.d(TAG, "Calling to setCommand")
                setCommandMode {
                    Log.d(TAG, "Command Rejected")
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
        Log.d(TAG, "Unable to destroy speechRecognizer.")
    }
}

private fun shutdownTextToSpeech(textToSpeech: SmartTextToSpeech) {
    Log.d(TAG, "Stopping text to speech instance: $textToSpeech")
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

@Composable
fun Menu(
    readAloudInitialValue: MutableState<Boolean>,
    onSpeakerIconClick: (on: Boolean) -> Unit,
    onSettingsIconClick: () -> Unit,
    onShareIconClick: () -> Unit,
    conversations: List<Conversation>
) {

    val volumeOn = remember {
        readAloudInitialValue
    }

    var showShareOption by remember {
        mutableStateOf(false)
    }

    showShareOption = conversations.size > 1

    if (showShareOption) {
        IconButton(onClick = {
            onShareIconClick()
        }) {
            Icon(Icons.Default.Share, "")
        }
    }

    IconButton(onClick = {
        volumeOn.value = !readAloudInitialValue.value
        onSpeakerIconClick(volumeOn.value)
    }) {
        Icon(
            painterResource(if (volumeOn.value) R.drawable.ic_volume_on else R.drawable.ic_volume_off),
            ""
        )
    }

    IconButton(onClick = {
        onSettingsIconClick()
    }) {
        Icon(Icons.Default.Settings, "")
    }
}


@Composable
fun NewChatFloatingButton(navigateToHome: (id: Long?, prompt: String?) -> Unit) {
    FloatingActionButton(
        modifier = Modifier.padding(bottom = 80.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.surface,
        shape = CircleShape,
        content = {
            Icon(
                Icons.Default.Add, ""
            )
        },
        onClick = { navigateToHome(null, null) })
}

@Composable
private fun BackPress() {
    var showToast by remember { mutableStateOf(false) }

    var backPressState by remember { mutableStateOf<BackPress>(BackPress.Idle) }
    val context = LocalContext.current

    if (showToast) {
        Toast.makeText(context, "Press again to exit", Toast.LENGTH_SHORT).show()
        showToast = false
    }

    LaunchedEffect(key1 = backPressState) {
        if (backPressState == BackPress.InitialTouch) {
            delay(2000)
            backPressState = BackPress.Idle
        }
    }

    BackHandler(backPressState == BackPress.Idle) {
        backPressState = BackPress.InitialTouch
        showToast = true
    }
}

@Composable
fun ErrorView(message: MutableState<String>) {
    var showError by remember { mutableStateOf(false) }
    showError = message.value.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message.value, Toast.LENGTH_SHORT).show()
    }
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
        refreshAll = {}
    )
}



