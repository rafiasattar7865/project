package com.example.campuslife.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "events")
data class Event(
    val title: String,
    val location: String,
    val date: String,
    val time: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Serializable
