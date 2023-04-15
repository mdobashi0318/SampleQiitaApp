package com.example.sampleqiitaapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sampleqiitaapp.data.Bookmark

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
abstract class QiitaAppRoomDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}