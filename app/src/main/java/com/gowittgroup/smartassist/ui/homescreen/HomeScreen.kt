package com.gowittgroup.smartassist.ui.homescreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
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
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.models.BackPress
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.*
import com.gowittgroup.smartassist.ui.rememberContentPaddingForScreen
import com.gowittgroup.smartassist.util.RecognitionCallbacks
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "HomeScreen"


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

    val textToSpeech: MutableState<TextToSpeech> = remember(context) {
        mutableStateOf(initTextToSpeech(context))
    }

    val speechRecognizer: SpeechRecognizer = remember(context) {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val commandRecognizer: SpeechRecognizer = remember(context) {
        SpeechRecognizer.createSpeechRecognizer(context)
    }
    var commandRecognizerIntent: Intent? = null
    val speechRecognizerIntent =
        initSpeechRecognizerIntent(
            speechRecognizer = speechRecognizer,
            onResult = { query ->
                Log.d(TAG, "Received voice query: $query")
                val handsFreeMode = viewModel.uiState.value?.handsFreeMode?.value ?: false

                if (handsFreeMode) {
                    viewModel.handsFreeModeStopListening { speechRecognizer.stopListening() }
                }

                sendQuery({
                    viewModel.ask(query) { content ->
                        speak(content = content, textToSpeech.value)
                    }
                }, isVoiceMessage = true, smartAnalytics = smartAnalytics)

                if (handsFreeMode) {
                    if (commandRecognizerIntent != null) {

                        viewModel.setCommandModeAfterReply {
                            Log.d(TAG, "STATES_HF calling to setCommand")
                            commandRecognizer.startListening(
                                commandRecognizerIntent
                            )
                        }
                    }
                }
            },
            onBeginSpeech = { viewModel.beginningSpeech() },
        )

    commandRecognizerIntent =
        initSpeechRecognizerIntent(
            isCommand = true,
            speechRecognizer = commandRecognizer,
            onResult = { command ->
                Log.d(TAG, "Received Command: $command")
                Log.d(TAG, "STATES_HF calling to releaseCommand")
                viewModel.releaseCommandMode { commandRecognizer.stopListening() }
                if (command.lowercase() == "ok buddy" || command.lowercase() == "okay buddy") {
                    Log.d(TAG, "Command Accepted")
                    viewModel.handsFreeModeStartListening {
                        Log.d(TAG, "Started Listening")
                        speechRecognizer.startListening(
                            speechRecognizerIntent
                        )
                    }
                } else {
                    Log.d(TAG, "STATES_HF calling to setCommand")
                    viewModel.setCommandMode {
                        Log.d(TAG, "Command Rejected")
                        commandRecognizer.startListening(
                            commandRecognizerIntent
                        )
                    }
                }
            },
            onBeginSpeech = {  },
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

    state.value?.let { uiState ->
        val conversations = uiState.conversations.filter { !it.forSystem }

        LaunchedEffect(key1 = uiState.handsFreeMode.value){
            if (uiState.handsFreeMode.value) {
                Log.d(TAG, "STATES_HF calling 1 to setCommand")
                viewModel.setCommandMode { commandRecognizer.startListening(commandRecognizerIntent) }
            } else {
                Log.d(TAG, "STATES_HF calling to releaseCommand")
                viewModel.releaseCommandMode { commandRecognizer.stopListening() }
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
                shutdownSpeechRecognizer(speechRecognizer)
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
                        handsFreeModeSection(uiState)
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
                                viewModel.stopListening { speechRecognizer.stopListening() }
                            },
                            onActionDown = {
                                viewModel.startListening {
                                    speechRecognizer.startListening(
                                        speechRecognizerIntent
                                    )
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
private fun handsFreeModeSection(uiState: HomeUiState) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.d(TAG, "STATES_HF ${uiState.speechRecognizerState}")
        when (uiState.speechRecognizerState) {
            SpeechRecognizerState.Listening -> {
                Log.d(TAG, "HandsFreeMode Start listening")
                ComposeLottieAnimation()
            }

            SpeechRecognizerState.Command -> Text(
                "Say, \"Okay buddy\", and the ask your query",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarSection(
    uiState: HomeUiState,
    onSpeakerIconClick: (on: Boolean) -> Unit,
    textToSpeech: MutableState<TextToSpeech>,
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
                        textToSpeech.value = initTextToSpeech(context)
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

private fun initTextToSpeech(context: Context): TextToSpeech {
    val textToSpeech = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "Text to speech initialized")
        } else {
            Log.d(TAG, "Text to speech initialization failed")
        }
    }
    Log.d(TAG, "Text to speech initialized instance:$textToSpeech")
    setLanguage(textToSpeech)
    return textToSpeech
}

private fun setLanguage(textToSpeech: TextToSpeech?) {

    val output = textToSpeech?.setLanguage(Locale.US)
    output?.let {
        if (it == TextToSpeech.LANG_MISSING_DATA || it == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.d(TAG, "Language is not supported")
        }
    }

}

private fun speak(content: String, textToSpeech: TextToSpeech?) {
    textToSpeech?.speak(content, TextToSpeech.QUEUE_FLUSH, null, null)
}


private fun initSpeechRecognizerIntent(
    speechRecognizer: SpeechRecognizer,
    onBeginSpeech: () -> Unit,
    onResult: (String) -> Unit,
    isCommand: Boolean = false
): Intent {

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

//        if (isCommand) {
//                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 120000)
//        }
    }



    speechRecognizer.setRecognitionListener(object : RecognitionCallbacks() {
        var tag = if (isCommand) "COMMAND: " else "QUERY: "
        override fun onBeginningOfSpeech() {
            Log.d(TAG, "$tag onBeginningOfSpeech()")
            onBeginSpeech()
        }

        override fun onError(error: Int) {
            Log.d(TAG, "$tag Error $error")
        }

        override fun onResults(results: Bundle?) {

            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Log.d(TAG, "$tag onResults ${data?.let { it[0] }}")
            data?.let {
                onResult(data[0])

            } ?: Log.d(TAG, "")
        }
    })

    return intent
}


private fun shutdownSpeechRecognizer(speechRecognizer: SpeechRecognizer) {
    try {
        speechRecognizer.destroy()
    } catch (e: Exception) {
        Log.d(TAG, "Unable to destroy speechRecognizer.")
    }

}

private fun shutdownTextToSpeech(textToSpeech: TextToSpeech) {
    Log.d(TAG, "Stopping text to speech instance: $textToSpeech")
    try {
        textToSpeech.stop()
        textToSpeech.shutdown()
    } catch (e: Exception) {
        Log.d(TAG, "Unable to shutdown textToSpeech.")
    }

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
