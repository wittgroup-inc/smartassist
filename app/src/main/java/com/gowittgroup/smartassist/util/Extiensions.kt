package com.gowittgroup.smartassist.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.gowittgroup.smartassist.SmartAssistApplication
import com.gowittgroup.smartassist.ui.SmartAssistApp
import java.text.SimpleDateFormat
import java.util.*


/**
 * Pattern: dd/MM/yyyy HH:mm:ss
 */
fun Date.formatToViewDateTimeDefaults(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

fun Context.isAndroidTV(): Boolean {
    return !packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}