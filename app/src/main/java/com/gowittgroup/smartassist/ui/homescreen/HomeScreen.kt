package com.gowittgroup.smartassist.ui.homescreen

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
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.models.BackPress
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
    viewModel: HomeViewModel,
    isExpanded: Boolean,
    showTopAppBar: Boolean,
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToHome: (id: Long?) -> Unit
) {
    val state = viewModel.uiState.observeAsState()
    val context: Context = LocalContext.current

    val textToSpeech: TextToSpeech = remember(context) {
        initTextToSpeech(context)
    }

    val speechRecognizer: SpeechRecognizer = remember(context) {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val speechRecognizerIntent = initSpeakRecognizerIntent(speechRecognizer, viewModel, textToSpeech)

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



    state.value?.let { uiState ->
        ErrorView(uiState.error)
        BackPress()
        LaunchedEffect(key1 = true) {
            Log.d(TAG, "Screen refreshed")
            viewModel.refreshAll()

            //Scrolling on new message.
            val position = uiState.conversations.size - 1
            if (position in 0 until uiState.conversations.size) {
                listState.scrollToItem(position)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                Log.d(TAG, "Disposing resources")
                shutdownSpeak(textToSpeech)
                shutdownSpeechRecognizer(speechRecognizer)
            }
        }

        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
        Scaffold(topBar = {
            HomeAppBar(
                actions = {
                    Menu(
                        readAloudInitialValue = uiState.readAloud,
                        onSpeakerIconClick = { isOn ->
                            viewModel.setReadAloud(isOn)
                        }, {
                            navigateToSettings()
                        })
                }, openDrawer = openDrawer,
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
                Column(modifier = Modifier.padding(padding)) {
                    if (uiState.conversations.isEmpty()) {
                        EmptyScreen(stringResource(R.string.empty_chat_secreen_message), Modifier.weight(1f), navigateToHistory = navigateToHistory)
                    } else {
                        ConversationView(
                            modifier = Modifier.weight(1f),
                            list = uiState.conversations,
                            listState = listState
                        )
                    }

                    if (uiState.showLoading) {
                        // TODO: can be handle later
                    }
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        ChatBar(state = uiState.textFieldValue,
                            hint = uiState.hint,
                            icon = if (uiState.micIcon) painterResource(R.drawable.ic_mic_on) else painterResource(R.drawable.ic_mic_off),
                            modifier = Modifier.padding(16.dp),
                            actionUp = { upAction(viewModel, speechRecognizer) },
                            actionDown = { downAction(viewModel, speechRecognizer, speechRecognizerIntent) },
                            onClick = { onClick(viewModel, textToSpeech) })

                        //Scrolling on new message.
                        SideEffect {
                            coroutineScope.launch {
                                val position = uiState.conversations.size - 1
                                if (position in 0 until uiState.conversations.size) {

                                    listState.scrollToItem(position)
                                }
                            }
                        }
                    }
                }
            })
    }
}


private fun initTextToSpeech(context: Context): TextToSpeech {
    val textToSpeech = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "Text to speech initialized")
        } else {
            Log.d(TAG, "Text to speech initialization failed")
        }
    }
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


private fun initSpeakRecognizerIntent(
    speechRecognizer: SpeechRecognizer,
    viewModel: HomeViewModel, textToSpeech: TextToSpeech
): Intent {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    speechRecognizer.setRecognitionListener(object : RecognitionCallbacks() {

        override fun onBeginningOfSpeech() {
            viewModel.beginningSpeech()
        }

        override fun onError(error: Int) {
            Log.d(TAG, "Error $error")
        }

        override fun onResults(results: Bundle?) {
            Log.d(TAG, "Result $results")
            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            data?.let {
                viewModel.ask(data[0]) { content -> viewModel.uiState.value?.readAloud?.let { speak(content, textToSpeech) } }
            } ?: Log.d(TAG, "")
        }
    })

    return intent
}

private fun shutdownSpeechRecognizer(speechRecognizer: SpeechRecognizer) {
    speechRecognizer.destroy()
}

private fun shutdownSpeak(textToSpeech: TextToSpeech) {
    Log.d(TAG, "Stopping text to speech $textToSpeech")
    textToSpeech.stop()
    textToSpeech.shutdown()
}

private fun onClick(viewModel: HomeViewModel, textToSpeech: TextToSpeech?) {
    viewModel.uiState.value?.let {
        viewModel.ask(it.textFieldValue.value.text) { content ->
            speak(content, textToSpeech)
        }
    }
}

private fun downAction(viewModel: HomeViewModel, speechRecognizer: SpeechRecognizer, speechRecognizerIntent: Intent) {
    viewModel.startListening()
    speechRecognizer.startListening(speechRecognizerIntent)
}

private fun upAction(viewModel: HomeViewModel, speechRecognizer: SpeechRecognizer) {
    speechRecognizer.stopListening()
    viewModel.stopListening()
}

@Composable
fun Menu(readAloudInitialValue: MutableState<Boolean>, onSpeakerIconClick: (on: Boolean) -> Unit, onSettingsIconClick: () -> Unit) {
    Log.d(TAG, "rendering menu")
    val volumeOn = remember {
        readAloudInitialValue
    }
    IconButton(onClick = {
        volumeOn.value = !readAloudInitialValue.value
        onSpeakerIconClick(volumeOn.value)
    }) {
        Icon(painterResource(if (volumeOn.value) R.drawable.ic_volume_on else R.drawable.ic_volume_off), "")
    }

    IconButton(onClick = {
        onSettingsIconClick()
    }) {
        Icon(Icons.Default.Settings, "")
    }
}


@Composable
fun NewChatFloatingButton(navigateToHome: (id: Long) -> Unit) {
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
        onClick = { navigateToHome(-1) })
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
fun ErrorView(message: String) {
    var showError by remember { mutableStateOf(false) }
    showError = message.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
