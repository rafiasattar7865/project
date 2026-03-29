package com.example.campuslife.database

import androidx.room.*
import com.example.campuslife.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id ASC")
    suspend fun getAll(): List<Note>

    @Insert
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
