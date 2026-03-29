package com.example.campuslife.database

import androidx.room.*
import com.example.campuslife.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    suspend fun getAll(): List<Task>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
