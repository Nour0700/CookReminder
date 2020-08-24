package com.example.android.reminder

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("dd/MM/yyyy hh:mm a").format(systemTime).toString()
}