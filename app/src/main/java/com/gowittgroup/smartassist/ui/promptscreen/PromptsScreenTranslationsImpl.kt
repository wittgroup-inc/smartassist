package com.gowittgroup.smartassist.ui.promptscreen

import android.content.Context
import com.gowittgroup.smartassist.R

class PromptsScreenTranslationsImpl(private val context: Context): PromptsScreenTranslations {
    override fun noInternetConnectionMessage(): String = context.getString(R.string.no_internet_conection)
}
