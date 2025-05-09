package com.example.timerecord.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM records ORDER BY id DESC LIMIT 5")
    fun getRecentRecords(): Flow<List<Record>>

    @Query("SELECT * FROM records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM records ORDER BY id DESC")
    suspend fun getAllRecordsSync(): List<Record>

    @Insert
    suspend fun insert(record: Record)

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)
} 