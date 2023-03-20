package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nework.dao.PostDao
import ru.netology.nework.entity.PostEntity

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDb : RoomDatabase(){
    abstract fun postDao(): PostDao
}