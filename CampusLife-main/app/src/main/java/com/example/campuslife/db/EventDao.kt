package com.example.campuslife.db

import androidx.room.*
import com.example.campuslife.model.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY id DESC")
    suspend fun getAll(): List<Event>

    @Insert
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}
