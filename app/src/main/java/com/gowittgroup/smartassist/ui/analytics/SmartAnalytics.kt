package com.gowittgroup.smartassist.ui.analytics

import android.os.Bundle

interface SmartAnalytics {

    fun logEvent(name: String, param: Bundle)

    object Event {
        val APP_OPEN = "app_open"
        val APP_EXIT = "app_exit"
        val USER_ON_SCREEN = "user_on_screen"
        val USER_CLIKED_ON = "user_clicked_on"
        val SEND_MESSAGE = "send_message"
    }

    object Param {
        val SCREEN_NAME = "screen_name"
        val ITEM_ID = "item_id"
        val ITEM_NAME = "item_name"
        val TIME_STAMP = "time_stamp"
    }
}
