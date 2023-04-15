package ru.netology.nework.entity

import androidx.room.TypeConverter
import ru.netology.nework.dto.EventType

class EventTypeConverter {
    @TypeConverter
    fun toEventType(value: String) = enumValueOf<EventType>(value)

    @TypeConverter
    fun fromEventType(value: EventType) = value.name
}