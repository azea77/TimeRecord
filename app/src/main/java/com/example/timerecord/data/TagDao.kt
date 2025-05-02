package com.example.timerecord.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: Tag)

    @Delete
    suspend fun delete(tag: Tag)
} 