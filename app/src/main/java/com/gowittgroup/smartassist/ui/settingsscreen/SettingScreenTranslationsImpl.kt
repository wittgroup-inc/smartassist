package com.gowittgroup.smartassist.ui.settingsscreen

import android.content.Context
import com.gowittgroup.smartassist.R

class SettingScreenTranslationsImpl(private val context: Context): SettingScreenTranslations {
    override fun noInternetConnectionMessage(): String = context.getString(R.string.no_internet_conection)
}
