package com.gowittgroup.smartassist.ui.settingsscreen

import android.content.Context
import com.gowittgroup.smartassist.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingScreenTranslationsImpl @Inject constructor(@ApplicationContext private val context: Context): SettingScreenTranslations {
    override fun noInternetConnectionMessage(): String = context.getString(R.string.no_internet_connection)
}
