package com.gowittgroup.smartassist.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.gowittgroup.core.logger.SmartLog
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

fun Context.openLink(link: String) {
    val uri = Uri.parse(link)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    try {
        this.startActivity(intent)
    } catch (ex: Exception) {
        SmartLog.e("Util", "Unable to open link.")
    }
}

fun Uri.isPdf(context: Context): Boolean {
    return if (scheme == "content") {
        val mimeType = context.contentResolver.getType(this)
        mimeType == "application/pdf"
    } else {
        toString().lowercase().endsWith(".pdf")
    }
}

fun Uri.isImage(context: Context): Boolean {
    return if (scheme == "content") {
        val mimeType = context.contentResolver.getType(this)
        mimeType?.startsWith("image/") == true
    } else {
        listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp").any { toString().lowercase().endsWith(it) }
    }
}
