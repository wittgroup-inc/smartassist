package com.gowittgroup.smartassist.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.gowittgroup.smartassist.R

fun copyTextToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, context.getString(R.string.text_copied_msg), Toast.LENGTH_SHORT).show()
}