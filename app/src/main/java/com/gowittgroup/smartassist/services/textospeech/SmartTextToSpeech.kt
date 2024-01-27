package com.gowittgroup.smartassist.services.textospeech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log

import com.gowittgroup.smartassist.services.speechrecognizer.SmartSpeechRecognizer
import java.util.Locale

class SmartTextToSpeech {
    private var _textToSpeech: TextToSpeech? = null

    private val textToSpeech: () -> TextToSpeech = {
        _textToSpeech ?: throw IllegalStateException()
    }

    fun initialize(context: Context) {
        _textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d(TAG, "Text to speech initialized")
            } else {
                Log.d(TAG, "Text to speech initialization failed")
            }
        }.also {
            setLanguage(it)
        }
    }

    private fun setLanguage(textToSpeech: TextToSpeech?) {
        val output = textToSpeech?.setLanguage(Locale.US)
        output.let {
            if (it == TextToSpeech.LANG_MISSING_DATA || it == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d(TAG, "Language is not supported")
            }
        }
    }

    fun stop() {
        execute("stop") {
            textToSpeech().stop()
        }
    }

    fun shutdown() {
        execute("shutdown") {
            textToSpeech().stop()
            textToSpeech().shutdown()
        }
        _textToSpeech = null
    }

    private fun <T> execute(methodName: String = "", action: () -> T): T? {
        return try {
            action()
        } catch (e: IllegalStateException) {
            Log.e(
                SmartSpeechRecognizer.TAG,
                "IllegalStateException caught while executing $methodName, please try initializing."
            )
            null
        }
    }

    fun speak(content: String) {
        execute("speak") {
            textToSpeech().speak(content, TextToSpeech.QUEUE_FLUSH, null, null)
        }

    }

    companion object {
        val TAG = TextToSpeech::class.simpleName
    }
}