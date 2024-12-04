package com.gowittgroup.smartassist.services.speechrecognizer

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.gowittgroup.core.logger.SmartLog
import java.util.Locale

class SmartSpeechRecognizer {

    private var _speechRecognizer: SpeechRecognizer? = null
    var isListening = false
        private set

    private val speechRecognizer: () -> SpeechRecognizer = {
        _speechRecognizer ?: throw IllegalStateException()
    }

    fun initialize(context: Context) {
        _speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    }

    fun setCallback(listener: SmartSpeechRecognitionCallbacks) {
        execute("setCallback") {
            speechRecognizer().setRecognitionListener(listener)
        }
    }


    fun startListening() {
        val intent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        execute("startListening") {
            speechRecognizer().startListening(intent)
            isListening = true
        }
    }

    fun stopListening() {
        execute("stopListening") {
            speechRecognizer().stopListening()
            isListening = false
        }
    }

    fun shutDown() {
        execute("shutDown") {
            speechRecognizer().stopListening()
            speechRecognizer().destroy()
            isListening = false
        }
        _speechRecognizer = null
    }

    private fun <T> execute(methodName: String = "", action: () -> T): T? {
        return try {
            action()
        } catch (e: IllegalStateException) {
            SmartLog.e(
                TAG,
                "IllegalStateException caught while executing $methodName, please try initializing."
            )
            null
        }
    }


    companion object {
        val TAG = SpeechRecognizer::class.simpleName
    }
}



