package ru.netology.nework.entity

import androidx.room.TypeConverter

class IdListConverter {

    @TypeConverter
    fun fromIds(ids: List<Int>) : String{
        return  ids.joinToString(",")
    }

    @TypeConverter
    fun toIds(data: String) : List<Int>{
        return data.split(",").map { it.toInt() }
    }
}