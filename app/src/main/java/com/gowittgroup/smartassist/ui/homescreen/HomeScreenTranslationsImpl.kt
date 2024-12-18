package com.gowittgroup.smartassist.ui.homescreen

import android.content.Context
import com.gowittgroup.smartassist.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HomeScreenTranslationsImpl @Inject constructor(@ApplicationContext private val context: Context) :
    HomeScreenTranslations {
    override fun noInternetConnectionMessage(): String =
        context.getString(R.string.no_internet_connection)

    override fun unableToGetReply(): String = context.getString(R.string.unableToGetReply)
    override fun listening(): String = context.getString(R.string.listening)
    override fun tapAndHoldToSpeak(): String = context.getString(R.string.tap_and_hold_to_speak)
    override fun startTyping(): String = context.getString(R.string.startTyping)
}
