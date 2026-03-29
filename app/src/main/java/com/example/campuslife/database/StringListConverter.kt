package com.example.campuslife.database

import androidx.room.TypeConverter

class StringListConverter {

    @TypeConverter
    fun fromStringList(list: MutableList<String>): String =
        if (list.isEmpty()) "" else list.joinToString("|SEP|")

    @TypeConverter
    fun toStringList(value: String): MutableList<String> =
        if (value.isEmpty()) mutableListOf() else value.split("|SEP|").toMutableList()
}
