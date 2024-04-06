package com.gowittgroup.smartassist.ui.faqscreen

import android.content.Context
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.settingsscreen.SettingScreenTranslations
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FaqScreenTranslationsImpl @Inject constructor(@ApplicationContext private val context: Context):
    FaqScreenTranslations {
    override fun noInternetConnectionMessage(): String = context.getString(R.string.no_internet_connection)
}
