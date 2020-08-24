package com.example.android.reminder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "cook")
data class Cook(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val name: String = "Cook",
    @ColumnInfo(name = "last_time_cooked")
    var lastTimeCooked: Long = System.currentTimeMillis()
)