package com.gowittgroup.smartassist.ui.analytics

import android.os.Bundle

interface SmartAnalytics {

    fun logEvent(name: String, param: Bundle)

    object Event {
        const val APP_OPEN = "app_open"
        const val APP_EXIT = "app_exit"
        const val USER_ON_SCREEN = "user_on_screen"
        const val USER_CLICKED_ON = "user_clicked_on"
        const val SEND_MESSAGE = "send_message"
    }

    object Param {
        const val SCREEN_NAME = "screen_name"
        const val ITEM_ID = "item_id"
        const val ITEM_NAME = "item_name"
        const val TIME_STAMP = "time_stamp"
    }
}
