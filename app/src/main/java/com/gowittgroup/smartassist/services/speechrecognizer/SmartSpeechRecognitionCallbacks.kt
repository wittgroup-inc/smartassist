package com.gowittgroup.smartassist.services.speechrecognizer

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.gowittgroup.core.logger.SmartLog

abstract class SmartSpeechRecognitionCallbacks : RecognitionListener {

    override fun onReadyForSpeech(params: Bundle?) {
        SmartLog.d(TAG, "onReadyForSpeech()")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        SmartLog.d(TAG, "onBufferReceived()")
    }

    override fun onEndOfSpeech() {
        SmartLog.d(TAG, "onEndOfSpeech()")
    }

    override fun onPartialResults(partialResults: Bundle?) {
        SmartLog.d(TAG, "onPartialResults()")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        SmartLog.d(TAG, "onEvent()")
    }

    override fun onSegmentResults(segmentResults: Bundle) {
        SmartLog.d(TAG, "onSegmentResults()")
    }

    override fun onEndOfSegmentedSession() {
        SmartLog.d(TAG, "onLanguageDetection()")
    }

    override fun onLanguageDetection(results: Bundle) {
        SmartLog.d(TAG, "onLanguageDetection()")
    }

    override fun onRmsChanged(rmsdB: Float) {
        SmartLog.d(TAG, "onRmsChanged()")
    }

    override fun onResults(results: Bundle?) {
        val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        SmartLog.d(TAG, "onResults()")
        onResults(data?.map { it } ?: listOf())
    }

    abstract fun onResults(results: List<String>)

    companion object {
        val TAG = SmartSpeechRecognitionCallbacks::class.simpleName
    }
}
