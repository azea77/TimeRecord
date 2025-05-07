package com.example.timerecord.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var startTime: Long,
    var endTime: Long,
    var task: String
) 