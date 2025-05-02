package com.example.timerecord.data

import kotlinx.coroutines.flow.Flow

class Repository(private val recordDao: RecordDao, private val tagDao: TagDao) {
    val allRecords: Flow<List<Record>> = recordDao.getAllRecords()
    val allTags: Flow<List<Tag>> = tagDao.getAllTags()

    suspend fun insertRecord(record: Record) {
        recordDao.insert(record)
    }

    suspend fun updateRecord(record: Record) {
        recordDao.update(record)
    }

    suspend fun deleteRecord(record: Record) {
        recordDao.delete(record)
    }

    suspend fun insertTag(tag: Tag) {
        tagDao.insert(tag)
    }

    suspend fun deleteTag(tag: Tag) {
        tagDao.delete(tag)
    }
} 