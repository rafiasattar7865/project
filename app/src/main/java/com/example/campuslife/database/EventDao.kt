package com.example.campuslife.database

import androidx.room.*
import com.example.campuslife.model.Event

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY id ASC")
    suspend fun getAll(): List<Event>

    @Insert
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}
