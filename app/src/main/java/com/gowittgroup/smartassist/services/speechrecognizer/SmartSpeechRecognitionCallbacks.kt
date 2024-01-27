package com.gowittgroup.smartassist.services.speechrecognizer

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

abstract class SmartSpeechRecognitionCallbacks : RecognitionListener {

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "onReadyForSpeech()")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(TAG, "onBufferReceived()")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech()")
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.d(TAG, "onPartialResults()")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "onEvent()")
    }

    override fun onSegmentResults(segmentResults: Bundle) {
        Log.d(TAG, "onSegmentResults()")
    }

    override fun onEndOfSegmentedSession() {
        Log.d(TAG, "onLanguageDetection()")
    }

    override fun onLanguageDetection(results: Bundle) {
        Log.d(TAG, "onLanguageDetection()")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(TAG, "onRmsChanged()")
    }

    override fun onResults(results: Bundle?) {
        val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Log.d(TAG, "onResults()")
        onResults(data?.map { it } ?: listOf())
    }

    abstract fun onResults(results: List<String>)

    companion object {
        val TAG = SmartSpeechRecognitionCallbacks::class.simpleName
    }
}
