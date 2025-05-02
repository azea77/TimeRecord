package com.example.timerecord.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.timerecord.data.AppDatabase
import com.example.timerecord.data.Record
import com.example.timerecord.data.Repository
import com.example.timerecord.data.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository
    val allRecords: Flow<List<Record>>
    val allTags: Flow<List<Tag>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = Repository(database.recordDao(), database.tagDao())
        allRecords = repository.allRecords
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
        repository.insertTag(tag)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        repository.deleteTag(tag)
    }
}