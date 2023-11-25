package com.jar89.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toTimeFormat(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}