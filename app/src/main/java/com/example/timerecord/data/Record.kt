package com.example.timerecord.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var startTime: String,
    var endTime: String,
    var task: String
) 