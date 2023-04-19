package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.PostEntity

@Database(entities = [PostEntity::class], version = 4)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}
