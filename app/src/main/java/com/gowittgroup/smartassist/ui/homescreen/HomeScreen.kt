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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.services.speechrecognizer.SmartSpeechRecognizer
import com.gowittgroup.smartassist.services.textospeech.SmartTextToSpeech
import com.gowittgroup.smartassist.models.BackPress
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.*
import com.gowittgroup.smartassist.ui.rememberContentPaddingForScreen
import com.gowittgroup.smartassist.services.speechrecognizer.SmartSpeechRecognitionCallbacks
import com.gowittgroup.smartassist.util.isAndroidTV
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

private val COMMAND_VARIATION = listOf("ok buddy", "okay buddy")
private const val SPEECH_RECOGNIZER_MAX_RETRY_TIME: Long = 5 * 60 * 1000


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    isExpanded: Boolean,
    showTopAppBar: Boolean,
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToPrompts: () -> Unit,
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    smartAnalytics: SmartAnalytics
) {

    val state = viewModel.uiState.observeAsState()
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

    var openHandsFreeAlertDialog by remember {
        mutableStateOf(false)
    }


    initSpeechRecognizerForHoldAndSpeak(speechRecognizerHoldAndSpeak, viewModel, textToSpeech, smartAnalytics)
    initSpeechRecognizerForHandsFree(speechRecognizerHandsFree, viewModel, textToSpeech, smartAnalytics, speechRecognizerHandsFreeCommand)
    intSpeechRecognizerForHandsFreeCommand(speechRecognizerHandsFreeCommand, viewModel, speechRecognizerHandsFree)

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

    state.value?.let { uiState ->
        val conversations = uiState.conversations.filter { !it.forSystem }

        LaunchedEffect(key1 = uiState.handsFreeMode.value) {
            openHandsFreeAlertDialog = context.isAndroidTV() && !uiState.handsFreeMode.value
            if (uiState.handsFreeMode.value) {
                Log.d(TAG, "Calling to setCommand")
                viewModel.setCommandMode { speechRecognizerHandsFreeCommand.startListening() }
            } else {
                Log.d(TAG, "Calling to releaseCommand")
                viewModel.releaseCommandMode { speechRecognizerHandsFreeCommand.stopListening() }
            }
        }

        LaunchedEffect(key1 = true) {
            Log.d(TAG, "Screen refreshed")
            viewModel.refreshAll()
            //Scrolling on new message.
            val position = conversations.size - 1
            if (position in conversations.indices) {
                listState.scrollToItem(position)
            }
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
        ErrorView(uiState.error).also { viewModel.resetErrorMessage() }
        Scaffold(
            topBar = {
                TopBarSection(
                    uiState = uiState,
                    onSpeakerIconClick = { isOn -> viewModel.setReadAloud(isOn) },
                    textToSpeech = textToSpeech,
                    context = context,
                    navigateToSettings = navigateToSettings,
                    openDrawer = openDrawer,
                    topAppBarState = topAppBarState,
                    scrollBehavior = scrollBehavior,
                    isExpanded = isExpanded
                )
                if (openHandsFreeAlertDialog) {
                    HandsFreeModeNotification(
                        message = stringResource(R.string.hands_free_alert_dialog_message),
                        onCancel = { openHandsFreeAlertDialog = false },
                        onOk = {
                            viewModel.setHandsFreeMode()
                            openHandsFreeAlertDialog = false
                        })
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
                        ChatBarSection(
                            uiState = uiState,
                            modifier = modifier,
                            onSend = {
                                sendQuery(
                                    send = {
                                        viewModel.ask { content ->
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
                                viewModel.stopListening { speechRecognizerHoldAndSpeak.stopListening() }
                            },
                            onActionDown = {
                                viewModel.startListening {
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
                readAloudInitialValue = uiState.readAloud,
                onSpeakerIconClick = { isOn ->
                    onSpeakerIconClick(isOn)
                    if (isOn) {
                        shutdownTextToSpeech(textToSpeech.value)
                        textToSpeech.value = SmartTextToSpeech().apply { initialize(context) }
                    } else {
                        shutdownTextToSpeech(textToSpeech.value)
                    }
                }, {
                    navigateToSettings()
                })
        }, openDrawer = openDrawer,
        topAppBarState = topAppBarState,
        scrollBehavior = scrollBehavior,
        isExpanded = isExpanded
    )
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
            if (error == 7 && shouldRetry) {
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
    viewModel: HomeViewModel,
    textToSpeech: MutableState<SmartTextToSpeech>,
    smartAnalytics: SmartAnalytics
) {
    initSpeechRecognizer(
        speechRecognizer = speechRecognizer,
        onResult = { query ->
            Log.d(TAG, "Received voice query: $query")
            sendQuery({
                viewModel.ask(query) { content ->
                    speak(content = content, textToSpeech.value)
                }
            }, isVoiceMessage = true, smartAnalytics = smartAnalytics)
        },
        onBeginSpeech = { viewModel.beginningSpeech() },
        retryDuration = 0
    )
}

private fun initSpeechRecognizerForHandsFree(
    speechRecognizerHandsFree: SmartSpeechRecognizer,
    viewModel: HomeViewModel,
    textToSpeech: MutableState<SmartTextToSpeech>,
    smartAnalytics: SmartAnalytics,
    commandRecognizer: SmartSpeechRecognizer
) {
    initSpeechRecognizer(
        speechRecognizer = speechRecognizerHandsFree,
        onResult = { query ->
            Log.d(TAG, "Received voice query: $query")
            val handsFreeMode = viewModel.uiState.value?.handsFreeMode?.value ?: false

            if (handsFreeMode) {
                viewModel.handsFreeModeStopListening { speechRecognizerHandsFree.stopListening() }
            }

            sendQuery({
                viewModel.ask(query) { content ->
                    speak(content = content, textToSpeech.value)
                }
            }, isVoiceMessage = true, smartAnalytics = smartAnalytics)

            if (handsFreeMode) {
                viewModel.setCommandModeAfterReply {
                    Log.d(TAG, "Calling to setCommand")
                    commandRecognizer.startListening()
                }
            }
        },
        onBeginSpeech = { viewModel.beginningSpeech() },
        retryDuration = 1 * 60 * 1000
    )
}

private fun intSpeechRecognizerForHandsFreeCommand(
    commandRecognizer: SmartSpeechRecognizer,
    viewModel: HomeViewModel,
    queryRecognizer: SmartSpeechRecognizer
) {
    initSpeechRecognizer(
        isCommand = true,
        speechRecognizer = commandRecognizer,
        onResult = { command ->
            Log.d(TAG, "Received Command: $command")
            viewModel.releaseCommandMode { commandRecognizer.stopListening() }
            if (COMMAND_VARIATION.contains(command.lowercase())) {
                Log.d(TAG, "Command Accepted")
                viewModel.handsFreeModeStartListening {
                    Log.d(TAG, "Started Listening")
                    queryRecognizer.startListening()
                }
            } else {
                Log.d(TAG, "Calling to setCommand")
                viewModel.setCommandMode {
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
    onSettingsIconClick: () -> Unit
) {
    Log.d(TAG, "rendering menu")
    val volumeOn = remember {
        readAloudInitialValue
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
