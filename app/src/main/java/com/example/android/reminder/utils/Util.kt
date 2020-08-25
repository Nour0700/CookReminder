package com.example.android.reminder.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat



@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("dd/MM/yyyy hh:mm a").format(systemTime).toString()
}

fun createRangeOfTen(number: Long): LongRange{
    val trunc = number / 86400000
    val lowerBound = trunc * 86400000
    val upperBound = lowerBound + 86399999

    // test stuff
    //val trunc = number / 10000
    //val lowerBound = trunc * 10000
    //val upperBound = lowerBound + 10000
    return lowerBound..upperBound
}