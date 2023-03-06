package com.wittgroup.smartassist.ui.homescreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.components.*
import com.wittgroup.smartassist.util.RecognitionCallbacks
import java.util.*

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSettings: () -> Unit
) {
    val state = viewModel.homeModel.observeAsState()
    val context: Context = LocalContext.current

    val textToSpeech: TextToSpeech = remember(context) {
        initTextToSpeech(context)
    }

    val speechRecognizer: SpeechRecognizer = remember(context) {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val speechRecognizerIntent = initSpeakRecognizerIntent(speechRecognizer, viewModel, textToSpeech)

    LaunchedEffect(key1 = true) {
        Log.d(TAG, "Screen refreshed")
        viewModel.refreshAll()
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "Disposing resources")
            shutdownSpeak(textToSpeech)
            shutdownSpeechRecognizer(speechRecognizer)
        }
    }

    state.value?.let { model ->
        Scaffold(topBar = {
            AppBar(title = "Smart Assist", actions = {
                Menu(
                    readAloudInitialValue = model.readAloud,
                    onSpeakerIconClick = { isOn ->
                        viewModel.setReadAloud(isOn)
                    }, {
                        navigateToSettings()
                    })
            })
        }, content = { padding ->
            Column {
                if (model.conversations.isEmpty()) {
                    EmptyScreen("Conversation will appear here.", Modifier.weight(1f))
                } else {
                    ConversationView(
                        modifier = Modifier.weight(1f),
                        list = model.conversations)
                }

                if (model.showLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        TripleDotProgressIndicator()
                    }
                }
                Box(
                    modifier = Modifier
                        .heightIn(min = 64.dp)
                        .padding(padding),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    ChatBar(state = model.textFieldValue,
                        hint = model.hint,
                        icon = if (model.micIcon) painterResource(R.drawable.ic_mic_on) else painterResource(R.drawable.ic_mic_off),
                        modifier = Modifier.padding(16.dp),
                        actionUp = { upAction(viewModel, speechRecognizer) },
                        actionDown = { downAction(viewModel, speechRecognizer, speechRecognizerIntent) },
                        onClick = { onClick(viewModel, textToSpeech) })
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
                viewModel.ask(data[0]) { content -> viewModel.homeModel.value?.readAloud?.let { speak(content, textToSpeech) } }
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
    viewModel.homeModel.value?.let {
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


