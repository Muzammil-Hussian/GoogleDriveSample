package com.google.drive.extension

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

fun Long.formatDate(
    dateFormat: String? = null,
    timeFormat: String? = null
): String {
    val useDateFormat = dateFormat ?: "dd/mm/y"
    val useTimeFormat = timeFormat ?: "hh:mm a"
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = this
    return DateFormat.format("$useDateFormat  | $useTimeFormat", cal).toString()
}