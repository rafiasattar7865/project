package com.example.campuslife.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks")
data class Task(
    val title: String,
    val category: String,
    val date: String,          // Format: "d/M/yyyy"
    val time: String,          // Format: "hh:mm a"
    val description: String,   // Added for the "Add a little note" field
    val isImportant: Boolean,   // Added for the ⭐ Importance switch
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Serializable