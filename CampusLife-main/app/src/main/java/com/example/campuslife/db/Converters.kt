package com.example.campuslife.db

import androidx.room.TypeConverter
import org.json.JSONArray

class Converters {

    @TypeConverter
    fun fromStringList(list: MutableList<String>): String {
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(data: String): MutableList<String> {
        val list = mutableListOf<String>()
        if (data.isEmpty()) return list
        val array = JSONArray(data)
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }
}
