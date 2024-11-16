package com.gowittgroup.smartassistlib.network

import android.util.Log
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

abstract class ChatEventSourceListener : EventSourceListener() {
    override fun onOpen(eventSource: EventSource, response: Response) {
        super.onOpen(eventSource, response)
        Log.d(TAG, "Connection Opened")
    }

    override fun onClosed(eventSource: EventSource) {
        super.onClosed(eventSource)
        Log.d(TAG, "Connection Closed")
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        super.onFailure(eventSource, t, response)
        Log.e(TAG, "On Failure -: ${response?.body}")
    }

    companion object {
        val TAG = ChatEventSourceListener::class.java.simpleName.toString()
    }
}
