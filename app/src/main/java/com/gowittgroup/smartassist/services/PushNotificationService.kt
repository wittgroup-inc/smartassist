package com.gowittgroup.smartassist.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gowittgroup.core.logger.SmartLog


class PushNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        SmartLog.d(TAG, "New token received.")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        SmartLog.d(TAG, "Message received ${message.data}")
    }

    companion object {
        val TAG = PushNotificationService::class.simpleName
    }
}