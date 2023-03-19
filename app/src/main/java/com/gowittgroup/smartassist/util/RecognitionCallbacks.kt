package com.gowittgroup.smartassist.util

import android.os.Bundle
import android.speech.RecognitionListener

abstract class RecognitionCallbacks: RecognitionListener {

    override fun onRmsChanged(rmsdB: Float) {

    }
    override fun onReadyForSpeech(params: Bundle?) {

    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onEndOfSpeech() {
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }
}
