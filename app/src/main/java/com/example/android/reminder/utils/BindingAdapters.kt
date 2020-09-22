package com.example.android.reminder.utils

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.reminder.R
import com.example.android.reminder.addFragment.TAG
import com.example.android.reminder.database.Cook

@BindingAdapter("lastDateCookedFormatted")
fun TextView.lastDateCookedFormatted(cook: Cook?){
    cook?.let {
        text = convertLongToDateString(cook.lastTimeCooked)
    }
}

@BindingAdapter("headerTextFormatted")
fun TextView.headerTextFormatted(days: Int?){
    days?.let {
        val str = if(days == 0)
            context.getString(R.string.recently_cooked_text)
        else
            "Cooked before $days days"
        text = str
    }
}