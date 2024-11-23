package com.gowittgroup.smartassist.ui.promptscreen

import android.content.Context
import com.gowittgroup.smartassist.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PromptsScreenTranslationsImpl @Inject constructor(@ApplicationContext private val context: Context) :
    PromptsScreenTranslations {
    override fun noInternetConnectionMessage(): String =
        context.getString(R.string.no_internet_connection)
}
