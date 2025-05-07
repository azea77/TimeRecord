package com.example.timerecord.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.timerecord.data.AppDatabase
import com.example.timerecord.data.Record
import com.example.timerecord.data.Repository
import com.example.timerecord.data.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository
    val recentRecords: Flow<List<Record>>
    val allTags: Flow<List<Tag>>
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        val database = AppDatabase.getDatabase(application)
        repository = Repository(database.recordDao(), database.tagDao())
        recentRecords = repository.recentRecords
        allTags = repository.allTags
    }

    fun insertRecord(record: Record) = viewModelScope.launch {
        repository.insertRecord(record)
    }

    fun updateRecord(record: Record) = viewModelScope.launch {
        repository.updateRecord(record)
    }

    fun deleteRecord(record: Record) = viewModelScope.launch {
        repository.deleteRecord(record)
    }

    fun insertTag(tag: Tag) = viewModelScope.launch {
        val time = dateFormat.format(Date())
        Log.i("TimeRecord", "==========================================")
        Log.i("TimeRecord", "Time: $time")
        Log.i("TimeRecord", "Action: INSERT TAG")
        Log.i("TimeRecord", "Tag Name: ${tag.name}")
        Log.i("TimeRecord", "==========================================")
        repository.insertTag(tag)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        repository.deleteTag(tag)
    }
}