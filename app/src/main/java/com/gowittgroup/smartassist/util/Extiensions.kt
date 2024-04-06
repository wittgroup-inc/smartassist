package com.gowittgroup.smartassist.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * Pattern: dd/MM/yyyy HH:mm:ss
 */
fun Date.formatToViewDateTimeDefaults(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

fun Context.isAndroidTV(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}

fun Context.share(content: String, subject: String, chooserTitle: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.setType("text/plain")
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    this.startActivity(Intent.createChooser(intent, chooserTitle))
}