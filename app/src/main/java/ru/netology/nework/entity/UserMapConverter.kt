package ru.netology.nework.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nework.dto.UserPreview

class UserMapConverter {
    @TypeConverter
    fun fromUsers(users: Map<Long,UserPreview>): String {
        return Gson().toJson(users)
    }

    @TypeConverter
    fun toUsers(data: String):Map<Long,UserPreview> {
        val mapType = object : TypeToken<Map<Long, UserPreview>>() {
        }.type
        return Gson().fromJson(data, mapType)
    }
}