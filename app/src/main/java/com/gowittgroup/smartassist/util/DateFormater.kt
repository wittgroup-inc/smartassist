package com.gowittgroup.smartassist.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("d, MMM yyyy h:mm a", Locale.getDefault())
    return format.format(date)
}