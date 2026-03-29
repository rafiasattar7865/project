package com.example.campuslife.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.campuslife.database.StringListConverter
import java.io.Serializable

@Entity(tableName = "notes")
@TypeConverters(StringListConverter::class)
data class Note(
    var title: String,
    var content: String,
    val date: String,
    val color: String,
    val attachmentUris: MutableList<String> = mutableListOf(),
    val attachmentNames: MutableList<String> = mutableListOf(),
    val links: MutableList<String> = mutableListOf(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Serializable