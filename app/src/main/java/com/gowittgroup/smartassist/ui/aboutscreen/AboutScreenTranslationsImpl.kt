package com.gowittgroup.smartassist.ui.settingsscreen

import android.content.Context
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.aboutscreen.AboutScreenTranslations
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AboutScreenTranslationsImpl @Inject constructor(@ApplicationContext private val context: Context):
    AboutScreenTranslations {
    override fun noInternetConnectionMessage(): String = context.getString(R.string.no_internet_connection)
}
